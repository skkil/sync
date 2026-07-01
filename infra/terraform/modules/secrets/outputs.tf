output "postgres_secret_arn" {
  value = aws_secretsmanager_secret.postgres.arn
}

output "server_app_secret_arn" {
  value = aws_secretsmanager_secret.server_app.arn
}

output "web_app_secret_arn" {
  value = aws_secretsmanager_secret.web_app.arn
}
