resource "aws_secretsmanager_secret" "postgres" {
  name                    = "${var.project_name}/${var.environment}/postgres/password"
  description             = "PostgreSQL credentials."
  kms_key_id              = var.kms_key_arn
  recovery_window_in_days = var.recovery_window_in_days

  tags = {
    Project     = var.project_name
    Environment = var.environment
  }
}

resource "aws_secretsmanager_secret_version" "postgres" {
  secret_id = aws_secretsmanager_secret.postgres.id
  secret_string = jsonencode({
    username = var.postgres_username
    password = var.postgres_password
    engine   = "postgres"
    host     = var.postgres_host
    port     = var.postgres_port
    dbname   = var.postgres_db_name
    jdbc_url = "jdbc:postgresql://${var.postgres_host}:${var.postgres_port}/${var.postgres_db_name}"
  })
}

resource "aws_secretsmanager_secret" "server_app" {
  name                    = "${var.project_name}/${var.environment}/server/app"
  description             = "Server application secrets (OAuth2, email, Slack)."
  kms_key_id              = var.kms_key_arn
  recovery_window_in_days = var.recovery_window_in_days

  tags = {
    Project     = var.project_name
    Environment = var.environment
  }
}

resource "aws_secretsmanager_secret_version" "server_app" {
  secret_id = aws_secretsmanager_secret.server_app.id
  secret_string = jsonencode({
    OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_ID     = var.google_client_id
    OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_SECRET = var.google_client_secret
    MAIL_USERNAME                                   = var.mail_username
    MAIL_PASSWORD                                   = var.mail_password
    SLACK_WEBHOOK_URL                               = var.slack_webhook_url
  })
}

resource "aws_secretsmanager_secret" "web_app" {
  name                    = "${var.project_name}/${var.environment}/web/app"
  description             = "Web application secrets (Better Auth)."
  kms_key_id              = var.kms_key_arn
  recovery_window_in_days = var.recovery_window_in_days

  tags = {
    Project     = var.project_name
    Environment = var.environment
  }
}

resource "aws_secretsmanager_secret_version" "web_app" {
  secret_id = aws_secretsmanager_secret.web_app.id
  secret_string = jsonencode({
    BETTER_AUTH_SECRET = var.better_auth_secret
  })
}
