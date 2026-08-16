#!/usr/bin/env bash
#
# Builds (optionally) and deploys web/server/nginx to production. Run this
# locally after `terraform apply` has created the ECR repositories and the
# EC2 instance.
#
# Usage:
#   scripts/cd/prod-deploy.sh              # deploy the image tag matching HEAD
#                                           # (must already be pushed to ECR)
#   scripts/cd/prod-deploy.sh --build       # build + push, then deploy
#   scripts/cd/prod-deploy.sh --tag abc123  # deploy a specific tag (e.g. rollback)
#
# --build and --tag can be combined: --build --tag abc123 builds and pushes
# under that tag instead of the current commit's.
#
# Requires: docker, aws CLI (authenticated), git, jq, and (with --build) a JDK
# capable of running ./gradlew.

export STAGE=prod
source .envrc

set -euo pipefail

# Without this, aws CLI pages every command's output through `less` when a
# real terminal is attached (even for --output text), which blocks this
# script waiting for a keypress instead of running non-interactively.
export AWS_PAGER=""

AWS_REGION="${AWS_REGION:-ap-northeast-2}"
PROJECT_NAME="${PROJECT_NAME:-sync}"
ENVIRONMENT="${ENVIRONMENT:-prod}"
APP_DOMAIN="${APP_DOMAIN:-sync.skkil.org}"

DO_BUILD=false
IMAGE_TAG=""

while [[ $# -gt 0 ]]; do
  case "$1" in
    --build) DO_BUILD=true; shift ;;
    --tag) IMAGE_TAG="$2"; shift 2 ;;
    *) echo "[ERROR] Unknown argument: $1" >&2; exit 1 ;;
  esac
done

IMAGE_TAG="${IMAGE_TAG:-$(git rev-parse --short=12 HEAD)}"

info() { echo "[INFO] $1"; }
error() { echo "[ERROR] $1" >&2; exit 1; }

info "Resolving ECR registry..."
ECR_REGISTRY="$(aws ecr describe-registry --query registryId --output text).dkr.ecr.${AWS_REGION}.amazonaws.com"

SERVER_IMAGE="${ECR_REGISTRY}/${PROJECT_NAME}-server:${IMAGE_TAG}"
WEB_IMAGE="${ECR_REGISTRY}/${PROJECT_NAME}-web:${IMAGE_TAG}"
NGINX_IMAGE="${ECR_REGISTRY}/${PROJECT_NAME}-nginx:${IMAGE_TAG}"

if [[ "$DO_BUILD" == true ]]; then
  info "Logging in to ECR ($ECR_REGISTRY)..."
  aws ecr get-login-password --region "$AWS_REGION" | docker login --username AWS --password-stdin "$ECR_REGISTRY"

  info "Fetching web secrets from Secrets Manager..."
  WEB_SECRET="$(aws secretsmanager get-secret-value --region "$AWS_REGION" --secret-id "${PROJECT_NAME}/${ENVIRONMENT}/web/app" --query SecretString --output text)"
  CHANNEL_TALK_PLUGIN_KEY="$(echo "$WEB_SECRET" | jq -r '.CHANNEL_TALK_PLUGIN_KEY // empty')"
  CAPTCHA_SITE_KEY="$(echo "$WEB_SECRET" | jq -r '.CAPTCHA_SITE_KEY // empty')"

  info "Building the server application..."
  cd "$PROJECT_ROOT_DIR/apps/server"
  ./gradlew build
  docker build -t "$SERVER_IMAGE" .

  info "Building the web application..."
  cd "$PROJECT_ROOT_DIR/apps/web"
  docker build \
      --build-context docs="$PROJECT_ROOT_DIR/docs" \
      --build-arg NEXT_PUBLIC_BACKEND_URL="https://${APP_DOMAIN}/api" \
      --build-arg NEXT_PUBLIC_SITE_URL="https://${APP_DOMAIN}" \
      --build-arg NEXT_PUBLIC_CHANNEL_TALK_PLUGIN_KEY="${CHANNEL_TALK_PLUGIN_KEY}" \
      --build-arg NEXT_PUBLIC_CAPTCHA_SITE_KEY="${CAPTCHA_SITE_KEY}" \
      --build-arg NEXT_PUBLIC_WEBSOCKET_ENABLED=false \
      -t "$WEB_IMAGE" .

  info "Building the nginx image..."
  cd "$PROJECT_ROOT_DIR/infra/nginx"
  docker build -t "$NGINX_IMAGE" .

  info "Pushing images (tag: ${IMAGE_TAG})..."
  docker push "$SERVER_IMAGE"
  docker push "$WEB_IMAGE"
  docker push "$NGINX_IMAGE"
else
  info "Skipping build (--build not passed) — deploying existing tag ${IMAGE_TAG}."
fi

info "Resolving production instance..."
INSTANCE_ID=$(aws ec2 describe-instances \
    --filters "Name=tag:Project,Values=${PROJECT_NAME}" "Name=tag:Environment,Values=${ENVIRONMENT}" "Name=instance-state-name,Values=running" \
    --query "Reservations[0].Instances[0].InstanceId" --output text)
[[ -n "$INSTANCE_ID" && "$INSTANCE_ID" != "None" ]] || error "Could not find a running ${ENVIRONMENT} instance"
info "Target instance: ${INSTANCE_ID}"

SCRIPT_B64=$(base64 -w0 "$PROJECT_ROOT_DIR/infra/ops/deploy.sh")
COMPOSE_B64=$(base64 -w0 "$PROJECT_ROOT_DIR/infra/compose/docker-compose.prod.yml")

info "Sending deploy command via SSM..."
COMMAND_ID=$(aws ssm send-command \
    --instance-ids "$INSTANCE_ID" \
    --document-name "AWS-RunShellScript" \
    --comment "sync ${ENVIRONMENT} deploy ${IMAGE_TAG}" \
    --parameters "{\"commands\":[\"echo ${SCRIPT_B64} | base64 -d > /tmp/deploy.sh && chmod +x /tmp/deploy.sh\",\"WEB_IMAGE=${WEB_IMAGE} SERVER_IMAGE=${SERVER_IMAGE} NGINX_IMAGE=${NGINX_IMAGE} DOCKER_COMPOSE_B64=${COMPOSE_B64} AWS_REGION=${AWS_REGION} PROJECT_NAME=${PROJECT_NAME} ENVIRONMENT=${ENVIRONMENT} APP_DOMAIN=${APP_DOMAIN} /tmp/deploy.sh\"]}" \
    --query "Command.CommandId" --output text)

info "Waiting for command ${COMMAND_ID} to finish..."
aws ssm wait command-executed --command-id "$COMMAND_ID" --instance-id "$INSTANCE_ID" || true

STATUS=$(aws ssm get-command-invocation --command-id "$COMMAND_ID" --instance-id "$INSTANCE_ID" --query "Status" --output text)
aws ssm get-command-invocation --command-id "$COMMAND_ID" --instance-id "$INSTANCE_ID" --query "StandardOutputContent" --output text
aws ssm get-command-invocation --command-id "$COMMAND_ID" --instance-id "$INSTANCE_ID" --query "StandardErrorContent" --output text >&2

[[ "$STATUS" == "Success" ]] || error "Deploy command finished with status: $STATUS"
info "Deploy successful (tag: ${IMAGE_TAG})."
