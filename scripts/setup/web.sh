#!/usr/bin/env bash

cat <<EOF > apps/web/.env.local
NEXT_PUBLIC_BACKEND_URL=http://localhost:8080
BETTER_AUTH_URL=http://localhost:3000
BETTER_AUTH_SECRET=$(openssl rand -base64 32)
EOF
