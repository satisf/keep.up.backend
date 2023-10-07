resource "aws_cognito_user_pool" "userpool" {
  name = "keep.up.users"

  password_policy {
    minimum_length    = "8"
    require_lowercase = false
    require_numbers   = false
    require_symbols   = false
    require_uppercase = false
  }

  lambda_config {
    pre_sign_up = aws_lambda_function.preSignup.arn
  }
}

resource "aws_lambda_permission" "cognito_allow_lambda"{
  statement_id  = "AllowExecutionFromCognito"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.preSignup.function_name
  principal     = "cognito-idp.amazonaws.com"
  source_arn    = aws_cognito_user_pool.userpool.arn
}