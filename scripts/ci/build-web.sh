#!/usr/bin/env bash

source .envrc

bash scripts/setup/web.sh

cd apps/web

pnpm install --frozen-lockfile

pnpm run orval

pnpm run format

pnpm run build
