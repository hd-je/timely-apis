#!/usr/bin/env bash
set -euo pipefail

APP_NAME="${APP_NAME:-timely-api}"
IMAGE_NAME="${IMAGE_NAME:-timely-api:latest}"
CONTAINER_NAME="${CONTAINER_NAME:-timely-api}"
NETWORK_NAME="${NETWORK_NAME:-}"
HOST_PORT="${HOST_PORT:-8081}"
CONTAINER_PORT="${CONTAINER_PORT:-8080}"

DB_URL="${DB_URL:-jdbc:mysql://mysql:3306/devdb?serverTimezone=Asia/Seoul&characterEncoding=UTF-8&useSSL=false&allowPublicKeyRetrieval=true}"
DB_USER="${DB_USER:-dev}"
DB_PASSWORD="${DB_PASSWORD:?DB_PASSWORD environment variable is required}"
JWT_SECRET="${JWT_SECRET:?JWT_SECRET environment variable is required}"
JWT_ACCESS_TOKEN_EXPIRATION_MS="${JWT_ACCESS_TOKEN_EXPIRATION_MS:-3600000}"

if [ -z "${NETWORK_NAME}" ]; then
  if docker network inspect ci >/dev/null 2>&1; then
    NETWORK_NAME="ci"
  else
    NETWORK_NAME="$(docker inspect mysql --format '{{range $name, $_ := .NetworkSettings.Networks}}{{println $name}}{{end}}' | head -n 1)"
  fi
fi

if [ -z "${NETWORK_NAME}" ]; then
  echo "[deploy] Docker network could not be detected. Set NETWORK_NAME explicitly."
  exit 1
fi

echo "[deploy] Building ${IMAGE_NAME}"
docker build -t "${IMAGE_NAME}" .

echo "[deploy] Removing previous ${CONTAINER_NAME} container if it exists"
docker rm -f "${CONTAINER_NAME}" >/dev/null 2>&1 || true

echo "[deploy] Starting ${CONTAINER_NAME}"
docker run -d \
  --name "${CONTAINER_NAME}" \
  --restart unless-stopped \
  --network "${NETWORK_NAME}" \
  -p "${HOST_PORT}:${CONTAINER_PORT}" \
  -e PROFILE=prod \
  -e DB_URL="${DB_URL}" \
  -e DB_USER="${DB_USER}" \
  -e DB_PASSWORD="${DB_PASSWORD}" \
  -e JWT_SECRET="${JWT_SECRET}" \
  -e JWT_ACCESS_TOKEN_EXPIRATION_MS="${JWT_ACCESS_TOKEN_EXPIRATION_MS}" \
  "${IMAGE_NAME}"

echo "[deploy] Waiting for application startup"
sleep 10

if docker ps --filter "name=^/${CONTAINER_NAME}$" --filter "status=running" --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
  echo "[deploy] ${APP_NAME} is running on host port ${HOST_PORT}"
else
  echo "[deploy] ${APP_NAME} failed to start"
  docker logs --tail 200 "${CONTAINER_NAME}" || true
  exit 1
fi
