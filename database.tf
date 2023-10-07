resource "aws_db_instance" "db" {
  allocated_storage    = 10
  engine               = "mysql"
  engine_version       = "5.7.33"
  instance_class       = "db.t2.micro"
  name                 = "db"
  username             = var.db_username
  password             = var.db_password
  skip_final_snapshot  = true
  publicly_accessible  = true
}

variable "db_username" {
  description = "The username for the DB master user"
  type        = string
  sensitive   = true
}
variable "db_password" {
  description = "The password for the DB master user"
  type        = string
  sensitive   = true
}