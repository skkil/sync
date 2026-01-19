#!/usr/bin/env bash

cd apps/web

pnpm install

pnpm run format

pnpm run build
