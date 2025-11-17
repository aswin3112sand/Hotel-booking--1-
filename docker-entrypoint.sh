#!/bin/sh
set -e

APP_JAR="/app/app.jar"
UPLOAD_DIR="${UPLOAD_DIR:-/app/uploads}"

# Ensure the upload directory exists and is writable by the spring user
mkdir -p "$UPLOAD_DIR"
chown -R spring:spring "$UPLOAD_DIR"

# Run the actual application (as root by default so uploads work everywhere)
exec java -jar "${APP_JAR}"
