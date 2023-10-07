terraform {
  required_providers {
    aws = {
      source = "hashicorp/aws"
    }
  }
}

provider "aws" {
  region = "eu-central-1"
}

data "archive_file" "preSignup_zip" {
  type          = "zip"
  source_file   = "./src/cognito/js/PreSignupLambda.js"
  output_path   = "./build/preSignup.zip"
}

resource "aws_lambda_function" "backend" {
  function_name = "backend"
  description   = "My awesome backend"

  s3_bucket = "keep-up-lambda"
  s3_key    = "keep.up.backend.jar"

  handler       = "org.springframework.cloud.function.adapter.aws.FunctionInvoker"
  runtime       = "java11"
  timeout       = 300
  memory_size   = 1769

  role = aws_iam_role.lambda_exec.arn
  environment {
    variables = {
      REGION = "eu-central-1"
      JWKS_URL = "https://cognito-idp.eu-central-1.amazonaws.com/${aws_cognito_user_pool.userpool.id}/.well-known/jwks.json"
      JWT_TOKEN_ISSUER = "https://cognito-idp.eu-central-1.amazonaws.com/${aws_cognito_user_pool.userpool.id}"
      SPRING_DATASOURCE_URL="jdbc:mysql://${aws_db_instance.db.endpoint}/${aws_db_instance.db.name}"
      SPRING_DATASOURCE_USERNAME=var.db_username
      SPRING_DATASOURCE_PASSWORD=var.db_password
    }
  }
}

resource "aws_lambda_function" "flyway" {
  function_name = "flyway"
  description   = "will keep the database structure up to date"

  s3_bucket = "flyway"
  s3_key    = "flyway-awslambda-0.4.0.jar"

  handler       = "crossroad0201.aws.flywaylambda.S3EventMigrationHandler"
  runtime       = "java8"
  timeout       = 300
  memory_size   = 512

  role = aws_iam_role.flyway_lambda_exec.arn

  environment {
    variables = {
      FLYWAY_USER = var.db_username
      FLYWAY_PASSWORD = var.db_password
      FLYWAY_URL = "jdbc:mysql://${aws_db_instance.db.endpoint}/${aws_db_instance.db.name}"
    }
  }
}

resource "aws_lambda_function" "preSignup" {
  function_name = "preSignup"
  description   = "confirms new users"

  handler       = "PreSignupLambda.handler"
  filename = "./build/preSignup.zip"
  runtime = "nodejs12.x"

  role = aws_iam_role.lambda_exec.arn
}

resource "aws_iam_role" "lambda_exec" {
  name = "serverless_lambda_execution_role"

  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": "sts:AssumeRole",
      "Principal": {
        "Service": "lambda.amazonaws.com"
      },
      "Effect": "Allow",
      "Sid": ""
    }
  ]
}
EOF
  managed_policy_arns = [
    "arn:aws:iam::aws:policy/service-role/AWSLambdaVPCAccessExecutionRole"
  ]
}

resource "aws_iam_role" "flyway_lambda_exec" {
  name = "flyway_lambda_execution_role"

  assume_role_policy = <<EOF
{
	"Version": "2012-10-17",
	"Statement": [{
		"Action": "sts:AssumeRole",
		"Principal": {
			"Service": "lambda.amazonaws.com"
		},
		"Effect": "Allow",
		"Sid": ""
	}]
}
EOF

  managed_policy_arns = [
    "arn:aws:iam::aws:policy/AmazonS3FullAccess",
    "arn:aws:iam::aws:policy/AmazonRDSFullAccess",
    "arn:aws:iam::aws:policy/service-role/AWSLambdaVPCAccessExecutionRole"
  ]
}

resource "aws_iam_policy" "cloudwatch_logs" {
  name        = "cloudwatch_logs"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = [
          "logs:*",
        ]
        Effect   = "Allow"
        Resource = "*"
      },
    ]
  })
}

resource "aws_iam_role_policy_attachment" "logs_to_flyway" {
  role       = aws_iam_role.flyway_lambda_exec.name
  policy_arn = aws_iam_policy.cloudwatch_logs.arn
}

resource "aws_iam_role_policy_attachment" "logs_to_lambda" {
  role       = aws_iam_role.lambda_exec.name
  policy_arn = aws_iam_policy.cloudwatch_logs.arn
}

resource "aws_lambda_permission" "allow_bucket" {
  statement_id  = "AllowExecutionFromS3Bucket"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.flyway.arn
  principal     = "s3.amazonaws.com"
  source_arn    = aws_s3_bucket.flyway_bucket.arn
}

resource "aws_s3_bucket" "flyway_bucket" {
  bucket = "flyway"
}

resource "aws_s3_bucket_notification" "flyway_bucket_notification" {
  bucket = aws_s3_bucket.flyway_bucket.id

  lambda_function {
    lambda_function_arn = aws_lambda_function.flyway.arn
    events = ["s3:ObjectCreated:*"]
    filter_suffix = ".sql"
  }
}