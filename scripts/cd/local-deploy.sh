#!/usr/bin/env bash

source .envrc

set -eou pipefail

AWS_SECRETSMANAGER_ENDPOINT="http://host.minikube.internal:4566"

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

info "Checking minikube status..."
if ! minikube status | grep -q "Running"; then
    info "Starting minikube..."
    minikube start --cpus=2 --memory=4096
    info "minikube started successfully"
else
    info "minikube is already running"
fi

info "Provisioning LocalStack resources with Terraform..."
cd "$PROJECT_ROOT_DIR/infra/terraform/environments/local"
terraform init -reconfigure -input=false
terraform plan -input=false
terraform apply -auto-approve -input=false

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

info "Installing External Secrets Operator..."
helm repo add external-secrets https://charts.external-secrets.io --force-update
helm repo update

helm upgrade --install external-secrets external-secrets/external-secrets \
    --version 0.10.7 \
    --namespace external-secrets \
    --create-namespace \
    --set extraEnv[0].name=AWS_SECRETSMANAGER_ENDPOINT \
    --set extraEnv[0].value="$AWS_SECRETSMANAGER_ENDPOINT" \
    --wait \
    --timeout 5m
info "External Secrets Operator ready"

info "Waiting for External Secrets Operator to be ready..."
kubectl rollout status deployment/external-secrets-webhook -n external-secrets --timeout=120s

info "Creating LocalStack credentials secret for External Secrets Operator..."
kubectl create secret generic localstack-credentials \
    --namespace external-secrets \
    --from-literal=access-key=skkil \
    --from-literal=secret-key=sync \
    --dry-run=client -o yaml | kubectl apply -f -

info "Applying cluster-level resources for Kubernetes..."
kubectl apply -k "$PROJECT_ROOT_DIR/infra/k8s/cluster/local"

info "Applying local overlay for Kubernetes..."
kubectl apply -k "$PROJECT_ROOT_DIR/infra/k8s/overlays/local"

info "Waiting for PostgreSQL to be ready..."
kubectl rollout status deployment/postgres -n sync --timeout=120s

info "Waiting for server deployment to be ready..."
kubectl rollout status deployment/server -n sync --timeout=120s

info "Waiting for web deployment to be ready..."
kubectl rollout status deployment/web -n sync --timeout=120s

info "Exposing the web application on port 3000..."
kubectl port-forward svc/web -n sync 3000:3000

info "Exposing the server application on port 8080..."
kubectl port-forward svc/server -n sync 8080:8080
