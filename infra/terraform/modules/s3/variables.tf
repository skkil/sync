variable "project_name" {
  type        = string
  description = "Name of the project, used as a prefix for resource names."
}

variable "environment" {
  type        = string
  description = "Deployment environment (e.g. local, staging, production)."
}

variable "bucket_name" {
  type        = string
  description = "Name of the S3 bucket for media storage."
}

variable "cors_allowed_origins" {
  type        = list(string)
  description = "List of origins allowed to make cross-origin requests to the bucket."
}
