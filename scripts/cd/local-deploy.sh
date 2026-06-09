#!/usr/bin/env bash

source .envrc

set -eou pipefail

info() {
    MESSAGE="$1"
    echo "[INFO] ${MESSAGE}"
}

error() {
    MESSAGE="$1"
    echo "[ERROR] ${MESSAGE}"
    exit 1
}

info "Checking requirements..."

for cmd in docker minikube kubectl terraform; do
    command -v "$cmd" >/dev/null 2>&1 || error "'$cmd' is not installed"
done

info "Starting LocalStack instance..."

docker compose -f "$PROJECT_ROOT_DIR/infra/localstack/docker-compose.yml" up -d

info "Provisioning LocalStack resources with Terraform..."
cd "$PROJECT_ROOT_DIR/infra/terraform/environments/local"
terraform init -reconfigure -input=false
terraform plan -input=false
terraform apply -auto-approve -input=false

info "Checking minikube status..."
if ! minikube status | grep -q "Running"; then
    info "Starting minikube..."
    minikube start --cpus=2 --memory=4096
    info "minikube started successfully"
else
    info "minikube is already running"
fi

info "Building the server application..."
cd "$PROJECT_ROOT_DIR/apps/server"
./gradlew build
docker build -t sync-server:local .

info "Uploading server image to minikube..."
minikube image load sync-server:local

info "Building the web application..."
cd "$PROJECT_ROOT_DIR/apps/web"
docker build \
    --build-arg NEXT_PUBLIC_BACKEND_URL=http://localhost:8080 \
    -t sync-web:local .

info "Uploading web image to minikube..."
minikube image load sync-web:local
