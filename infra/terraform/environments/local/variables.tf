variable "project_name" {
  type        = string
  default     = "sync"
  description = "Name of the project, used as a prefix for all resource names."
}

variable "environment" {
  type        = string
  default     = "local"
  description = "Deployment environment. Controls resource naming and behaviour (e.g. secret recovery window)."
}

variable "s3_media_bucket_name" {
  type        = string
  default     = "skkil-sync-media"
  description = "Name of the S3 bucket used to store user-uploaded media."
}

variable "s3_cors_allowed_origins" {
  type        = list(string)
  default     = ["http://localhost:3000"]
  description = "Origins permitted to make cross-origin requests to the media bucket."
}

variable "postgres_host" {
  type        = string
  default     = "postgres.sync.svc.cluster.local"
  description = "Hostname of the PostgreSQL server (Kubernetes service DNS in-cluster)."
}

variable "postgres_db_name" {
  type        = string
  default     = "sync"
  description = "Name of the PostgreSQL database."
}

variable "postgres_username" {
  type        = string
  default     = "skkil"
  description = "PostgreSQL login username."
}

variable "postgres_password" {
  type        = string
  sensitive   = true
  default     = "password"
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
