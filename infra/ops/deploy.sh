#!/usr/bin/env bash
#
# Runs ON the production EC2 instance, invoked remotely via
# `aws ssm send-command` from scripts/cd/prod-deploy.sh. Fetches the latest
# secrets from Secrets Manager, writes the env files docker compose reads,
# then pulls and restarts the web/server/nginx containers.
#
# Required environment variables (set by the caller, e.g. the SSM command):
#   WEB_IMAGE           Full ECR image reference for the web app, with tag.
#   SERVER_IMAGE        Full ECR image reference for the server app, with tag.
#   NGINX_IMAGE         Full ECR image reference for the nginx reverse proxy, with tag.
#   DOCKER_COMPOSE_B64  base64 of infra/compose/docker-compose.prod.yml, written
#                       to $APP_DIR/docker-compose.yml before pulling images.
#
# Optional:
#   AWS_REGION    Defaults to ap-northeast-2.
#   PROJECT_NAME  Defaults to sync.
#   ENVIRONMENT   Defaults to prod.
#   APP_DOMAIN    Defaults to sync.skkil.org.

set -euo pipefail

: "${WEB_IMAGE:?WEB_IMAGE is required}"
: "${SERVER_IMAGE:?SERVER_IMAGE is required}"
: "${NGINX_IMAGE:?NGINX_IMAGE is required}"
: "${DOCKER_COMPOSE_B64:?DOCKER_COMPOSE_B64 is required}"

AWS_REGION="${AWS_REGION:-ap-northeast-2}"
PROJECT_NAME="${PROJECT_NAME:-sync}"
ENVIRONMENT="${ENVIRONMENT:-prod}"
APP_DOMAIN="${APP_DOMAIN:-sync.skkil.org}"
APP_DIR="/opt/sync"

info() { echo "[INFO] $1"; }

secret() {
    aws secretsmanager get-secret-value \
        --region "$AWS_REGION" \
        --secret-id "${PROJECT_NAME}/${ENVIRONMENT}/$1" \
        --query SecretString --output text
}

info "Fetching secrets from Secrets Manager..."
POSTGRES_SECRET="$(secret postgres/password)"
SERVER_SECRET="$(secret server/app)"

ECR_REGISTRY="$(echo "$WEB_IMAGE" | cut -d/ -f1)"

info "Logging in to ECR..."
aws ecr get-login-password --region "$AWS_REGION" | docker login --username AWS --password-stdin "$ECR_REGISTRY"

info "Writing env files..."
cat > "$APP_DIR/.env" <<EOF
WEB_IMAGE=${WEB_IMAGE}
SERVER_IMAGE=${SERVER_IMAGE}
NGINX_IMAGE=${NGINX_IMAGE}
DOMAIN_NAME=${APP_DOMAIN}
EOF

cat > "$APP_DIR/server.env" <<EOF
DATABASE_URL=$(echo "$POSTGRES_SECRET" | jq -r .jdbc_url)
DATABASE_USERNAME=$(echo "$POSTGRES_SECRET" | jq -r .username)
DATABASE_PASSWORD=$(echo "$POSTGRES_SECRET" | jq -r .password)
OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_ID=$(echo "$SERVER_SECRET" | jq -r .OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_ID)
OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_SECRET=$(echo "$SERVER_SECRET" | jq -r .OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_SECRET)
OAUTH2_CLIENT_REGISTRATION_GITHUB_CLIENT_ID=$(echo "$SERVER_SECRET" | jq -r .OAUTH2_CLIENT_REGISTRATION_GITHUB_CLIENT_ID)
OAUTH2_CLIENT_REGISTRATION_GITHUB_CLIENT_SECRET=$(echo "$SERVER_SECRET" | jq -r .OAUTH2_CLIENT_REGISTRATION_GITHUB_CLIENT_SECRET)
MAIL_USERNAME=$(echo "$SERVER_SECRET" | jq -r .MAIL_USERNAME)
MAIL_PASSWORD=$(echo "$SERVER_SECRET" | jq -r .MAIL_PASSWORD)
SLACK_WEBHOOK_URL=$(echo "$SERVER_SECRET" | jq -r .SLACK_WEBHOOK_URL)
CHANNEL_TALK_SECRET_KEY=$(echo "$SERVER_SECRET" | jq -r '.CHANNEL_TALK_SECRET_KEY // empty')
CAPTCHA_SECRET_KEY=$(echo "$SERVER_SECRET" | jq -r '.CAPTCHA_SECRET_KEY // empty')
OPENAI_API_KEY=$(echo "$SERVER_SECRET" | jq -r '.OPENAI_API_KEY // empty')
ADMIN_EMAIL=$(echo "$SERVER_SECRET" | jq -r '.ADMIN_EMAIL // empty')
ADMIN_PASSWORD=$(echo "$SERVER_SECRET" | jq -r '.ADMIN_PASSWORD // empty')
# server.env is a flat KEY=VALUE-per-line file (docker compose env_file format,
# no multi-line values) but these are PEMs. AgentSigningKeys.parsePem strips
# all whitespace before base64-decoding, so collapsing onto one line here is
# safe and keeps the PEM from breaking every line after it in server.env.
APP_AGENT_RSA_PRIVATE_KEY=$(echo "$SERVER_SECRET" | jq -r '.APP_AGENT_RSA_PRIVATE_KEY // empty' | tr -d '\n')
APP_AGENT_RSA_PUBLIC_KEY=$(echo "$SERVER_SECRET" | jq -r '.APP_AGENT_RSA_PUBLIC_KEY // empty' | tr -d '\n')
APP_AGENT_CHATGPT_REDIRECT_URI=$(echo "$SERVER_SECRET" | jq -r '.APP_AGENT_CHATGPT_REDIRECT_URI // empty')
APP_CORS_ALLOWED_ORIGINS=https://${APP_DOMAIN}
APP_RATE_LIMIT_TRUSTED_PROXY_COUNT=1
APP_FRONTEND_BASE_URL=https://${APP_DOMAIN}
APP_OAUTH2_FRONTEND_REDIRECT_URI=https://${APP_DOMAIN}
APP_AGENT_ISSUER_URI=https://${APP_DOMAIN}
OAUTH2_CLIENT_REGISTRATION_GOOGLE_REDIRECT_URI=https://${APP_DOMAIN}/api/login/oauth2/code/google
OAUTH2_CLIENT_REGISTRATION_GITHUB_REDIRECT_URI=https://${APP_DOMAIN}/api/login/oauth2/code/github
WEBSOCKET_ENABLED=false
AI_FEATURES_ENABLED=true
AI_CHAT_PROVIDER=none
AI_EMBEDDING_PROVIDER=openai
ENABLE_TELEMETRY=false
EOF

chmod 600 "$APP_DIR/server.env"

# nginx won't start without cert files at the paths its config references, so
# seed a dummy self-signed cert if none exists yet. infra/ops/certbot.sh
# later replaces it with a real Let's Encrypt lineage. Idempotent: once a real
# (or dummy) cert is present this is skipped, so deploys never clobber it.
CERT_DIR="$APP_DIR/certbot/conf/live/$APP_DOMAIN"
if [ ! -f "$CERT_DIR/fullchain.pem" ]; then
    info "Seeding dummy self-signed cert for ${APP_DOMAIN}..."
    mkdir -p "$CERT_DIR"
    openssl req -x509 -nodes -newkey rsa:2048 -days 1 \
        -keyout "$CERT_DIR/privkey.pem" \
        -out "$CERT_DIR/fullchain.pem" \
        -subj "/CN=$APP_DOMAIN"
fi

info "Writing docker-compose.yml..."
echo "$DOCKER_COMPOSE_B64" | base64 -d > "$APP_DIR/docker-compose.yml"

info "Pulling images..."
cd "$APP_DIR"
docker compose pull web server nginx

info "Restarting containers..."
docker compose up -d --remove-orphans

info "Pruning old images..."
docker image prune -f

info "Deploy complete: web=${WEB_IMAGE} server=${SERVER_IMAGE} nginx=${NGINX_IMAGE}"
