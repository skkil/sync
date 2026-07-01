variable "project_name" {
  type        = string
  description = "Name of the project, used as a prefix for resource names."
}

variable "environment" {
  type        = string
  description = "Deployment environment (e.g. local, staging, production)."
}

variable "kms_key_arn" {
  type        = string
  description = "ARN of the KMS key used to encrypt secrets."
}

variable "recovery_window_in_days" {
  type        = number
  default     = 7
  description = "Days before a deleted secret is permanently destroyed. Set to 0 in local/dev to allow immediate re-creation."
}

variable "postgres_host" {
  type        = string
  description = "Hostname of the PostgreSQL server."
}

variable "postgres_port" {
  type        = number
  default     = 5432
  description = "Port the PostgreSQL server listens on."
}

variable "postgres_db_name" {
  type        = string
  description = "Name of the PostgreSQL database."
}

variable "postgres_username" {
  type        = string
  description = "PostgreSQL login username."
}

variable "postgres_password" {
  type        = string
  sensitive   = true
  description = "PostgreSQL login password."
}

variable "google_client_id" {
  type        = string
  sensitive   = true
  description = "Google OAuth2 client ID for social login."
}

variable "google_client_secret" {
  type        = string
  sensitive   = true
  description = "Google OAuth2 client secret for social login."
}

variable "mail_username" {
  type        = string
  sensitive   = true
  description = "SMTP username for sending application emails."
}

variable "mail_password" {
  type        = string
  sensitive   = true
  description = "SMTP password (or app password) for sending application emails."
}

variable "slack_webhook_url" {
  type        = string
  sensitive   = true
  default     = ""
  description = "Slack incoming webhook URL for notifications. Leave empty to disable."
}

variable "better_auth_secret" {
  type        = string
  sensitive   = true
  description = "Secret key used by Better Auth to sign sessions and tokens."
}
