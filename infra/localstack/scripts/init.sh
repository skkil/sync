#!/usr/bin/env bash

set -euo pipefail

export AWS_ACCESS_KEY_ID=test
export AWS_SECRET_ACCESS_KEY=test

awslocal s3 mb s3://skkil-sync-media
