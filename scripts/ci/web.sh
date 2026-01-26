#!/usr/bin/env bash

bash scripts/setup/web.sh

cd apps/web

pnpm install

pnpm run format

pnpm run build
