terraform {
  required_version = ">= 1.6"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }

  backend "local" {}
}

provider "aws" {
  region     = "us-east-1"
  access_key = "skkil"
  secret_key = "sync"

  endpoints {
    kms            = "http://localhost:4566"
    s3             = "http://localhost:4566"
    secretsmanager = "http://localhost:4566"
  }

  skip_credentials_validation = true
  skip_metadata_api_check     = true
  skip_requesting_account_id  = true
  s3_use_path_style           = true
}

module "kms" {
  source       = "../../modules/kms"
  project_name = var.project_name
  environment  = var.environment
}

module "s3" {
  source               = "../../modules/s3"
  project_name         = var.project_name
  environment          = var.environment
  bucket_name          = var.s3_media_bucket_name
  cors_allowed_origins = var.s3_cors_allowed_origins
}

module "secrets" {
  source                  = "../../modules/secrets"
  project_name            = var.project_name
  environment             = var.environment
  kms_key_arn             = module.kms.key_arn
  postgres_host           = var.postgres_host
  postgres_db_name        = var.postgres_db_name
  postgres_username       = var.postgres_username
  postgres_password       = var.postgres_password
  google_client_id        = var.google_client_id
  google_client_secret    = var.google_client_secret
  mail_username           = var.mail_username
  mail_password           = var.mail_password
  slack_webhook_url       = var.slack_webhook_url
  better_auth_secret      = var.better_auth_secret
}
