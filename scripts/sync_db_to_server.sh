#!/usr/bin/env bash
set -euo pipefail

usage() {
  cat <<'EOF'
Usage:
  scripts/sync_db_to_server.sh

Environment variables:
  SSH_TARGET              Remote SSH target. Default: root@47.95.236.177
  SSH_PORT                Remote SSH port. Default: 22

  LOCAL_DB_HOST           Local MySQL host. Default: 127.0.0.1
  LOCAL_DB_PORT           Local MySQL port. Default: 3306
  LOCAL_DB_NAME           Local database name. Default: XYJ
  LOCAL_DB_USER           Local database user. Default: root
  LOCAL_DB_PASSWORD       Local database password. If empty, the script prompts.

  REMOTE_DB_HOST          Remote MySQL host from server side. Default: 127.0.0.1
  REMOTE_DB_PORT          Remote MySQL port. Default: 3306
  REMOTE_DB_NAME          Remote database name. Default: XYJ
  REMOTE_DB_USER          Remote database user. Default: XYJ
  REMOTE_DB_PASSWORD      Remote database password. If empty, the script prompts.

  REMOTE_TMP_DIR          Remote temporary directory. Default: /tmp
  KEEP_LOCAL_DUMP         Keep local dump file when set to 1. Default: 0
  KEEP_REMOTE_DUMP        Keep remote uploaded dump when set to 1. Default: 0
  SKIP_REMOTE_BACKUP      Skip backing up remote database when set to 1. Default: 0

Examples:
  LOCAL_DB_PASSWORD=123456 REMOTE_DB_PASSWORD=123456 scripts/sync_db_to_server.sh

  SSH_TARGET=root@47.95.236.177 \
  LOCAL_DB_USER=root LOCAL_DB_PASSWORD=123456 \
  REMOTE_DB_USER=XYJ REMOTE_DB_PASSWORD=123456 \
  scripts/sync_db_to_server.sh
EOF
}

if [[ "${1:-}" == "-h" || "${1:-}" == "--help" ]]; then
  usage
  exit 0
fi

require_command() {
  if ! command -v "$1" >/dev/null 2>&1; then
    echo "Missing required command: $1" >&2
    exit 1
  fi
}

prompt_secret_if_empty() {
  local var_name="$1"
  local prompt="$2"
  if [[ -z "${!var_name:-}" ]]; then
    read -r -s -p "$prompt: " "$var_name"
    export "$var_name"
    echo
  fi
}

require_command mysqldump
require_command mysql
require_command ssh
require_command scp

SSH_TARGET="${SSH_TARGET:-root@47.95.236.177}"
SSH_PORT="${SSH_PORT:-22}"

LOCAL_DB_HOST="${LOCAL_DB_HOST:-127.0.0.1}"
LOCAL_DB_PORT="${LOCAL_DB_PORT:-3306}"
LOCAL_DB_NAME="${LOCAL_DB_NAME:-XYJ}"
LOCAL_DB_USER="${LOCAL_DB_USER:-root}"

REMOTE_DB_HOST="${REMOTE_DB_HOST:-127.0.0.1}"
REMOTE_DB_PORT="${REMOTE_DB_PORT:-3306}"
REMOTE_DB_NAME="${REMOTE_DB_NAME:-XYJ}"
REMOTE_DB_USER="${REMOTE_DB_USER:-XYJ}"
REMOTE_TMP_DIR="${REMOTE_TMP_DIR:-/tmp}"

KEEP_LOCAL_DUMP="${KEEP_LOCAL_DUMP:-0}"
KEEP_REMOTE_DUMP="${KEEP_REMOTE_DUMP:-0}"
SKIP_REMOTE_BACKUP="${SKIP_REMOTE_BACKUP:-0}"

prompt_secret_if_empty LOCAL_DB_PASSWORD "Local MySQL password for ${LOCAL_DB_USER}@${LOCAL_DB_HOST}"
prompt_secret_if_empty REMOTE_DB_PASSWORD "Remote MySQL password for ${REMOTE_DB_USER}@${REMOTE_DB_HOST}"

timestamp="$(date +%Y%m%d_%H%M%S)"
local_dump="$(pwd)/${LOCAL_DB_NAME}_local_${timestamp}.sql"
remote_dump="${REMOTE_TMP_DIR}/${LOCAL_DB_NAME}_local_${timestamp}.sql"
remote_backup="${REMOTE_TMP_DIR}/${REMOTE_DB_NAME}_server_backup_before_${timestamp}.sql"

cleanup() {
  if [[ "${KEEP_LOCAL_DUMP}" != "1" && -f "${local_dump}" ]]; then
    rm -f "${local_dump}"
  fi
}
trap cleanup EXIT

echo "==> Dump local database ${LOCAL_DB_NAME} from ${LOCAL_DB_USER}@${LOCAL_DB_HOST}:${LOCAL_DB_PORT}"
MYSQL_PWD="${LOCAL_DB_PASSWORD}" mysqldump \
  -h "${LOCAL_DB_HOST}" \
  -P "${LOCAL_DB_PORT}" \
  -u "${LOCAL_DB_USER}" \
  --default-character-set=utf8mb4 \
  --single-transaction \
  --routines \
  --triggers \
  --events \
  --add-drop-table \
  --set-gtid-purged=OFF \
  "${LOCAL_DB_NAME}" > "${local_dump}"

echo "==> Upload dump to ${SSH_TARGET}:${remote_dump}"
scp -P "${SSH_PORT}" "${local_dump}" "${SSH_TARGET}:${remote_dump}"

echo "==> Import into remote database ${REMOTE_DB_NAME} as ${REMOTE_DB_USER}"
ssh -p "${SSH_PORT}" "${SSH_TARGET}" \
  "REMOTE_DB_HOST='${REMOTE_DB_HOST}' REMOTE_DB_PORT='${REMOTE_DB_PORT}' REMOTE_DB_NAME='${REMOTE_DB_NAME}' REMOTE_DB_USER='${REMOTE_DB_USER}' REMOTE_DB_PASSWORD='${REMOTE_DB_PASSWORD}' REMOTE_DUMP='${remote_dump}' REMOTE_BACKUP='${remote_backup}' SKIP_REMOTE_BACKUP='${SKIP_REMOTE_BACKUP}' KEEP_REMOTE_DUMP='${KEEP_REMOTE_DUMP}' bash -s" <<'REMOTE_SCRIPT'
set -euo pipefail

mysql_base=(mysql -h "${REMOTE_DB_HOST}" -P "${REMOTE_DB_PORT}" -u "${REMOTE_DB_USER}")
dump_base=(mysqldump -h "${REMOTE_DB_HOST}" -P "${REMOTE_DB_PORT}" -u "${REMOTE_DB_USER}")

echo "==> Ensure remote database exists"
MYSQL_PWD="${REMOTE_DB_PASSWORD}" "${mysql_base[@]}" -e "CREATE DATABASE IF NOT EXISTS \`${REMOTE_DB_NAME}\` DEFAULT CHARACTER SET utf8mb4 DEFAULT COLLATE utf8mb4_unicode_ci;"

if [[ "${SKIP_REMOTE_BACKUP}" != "1" ]]; then
  echo "==> Backup current remote database to ${REMOTE_BACKUP}"
  MYSQL_PWD="${REMOTE_DB_PASSWORD}" "${dump_base[@]}" \
    --default-character-set=utf8mb4 \
    --single-transaction \
    --routines \
    --triggers \
    --events \
    "${REMOTE_DB_NAME}" > "${REMOTE_BACKUP}"
fi

echo "==> Drop existing tables in ${REMOTE_DB_NAME}"
drop_sql="$(MYSQL_PWD="${REMOTE_DB_PASSWORD}" "${mysql_base[@]}" -N -B -e "
SELECT CONCAT('DROP TABLE IF EXISTS \`', table_name, '\`;')
FROM information_schema.tables
WHERE table_schema = '${REMOTE_DB_NAME}' AND table_type = 'BASE TABLE';
")"

if [[ -n "${drop_sql}" ]]; then
  {
    echo "SET FOREIGN_KEY_CHECKS=0;"
    echo "${drop_sql}"
    echo "SET FOREIGN_KEY_CHECKS=1;"
  } | MYSQL_PWD="${REMOTE_DB_PASSWORD}" "${mysql_base[@]}" "${REMOTE_DB_NAME}"
fi

echo "==> Import local dump"
MYSQL_PWD="${REMOTE_DB_PASSWORD}" "${mysql_base[@]}" \
  --default-character-set=utf8mb4 \
  "${REMOTE_DB_NAME}" < "${REMOTE_DUMP}"

echo "==> Verify imported data"
MYSQL_PWD="${REMOTE_DB_PASSWORD}" "${mysql_base[@]}" -N -B "${REMOTE_DB_NAME}" -e "
SELECT 'tables', COUNT(*) FROM information_schema.tables WHERE table_schema = '${REMOTE_DB_NAME}';
SELECT 'users', COUNT(*) FROM users;
SELECT 'packages', COUNT(*) FROM packages;
" || true

if [[ "${KEEP_REMOTE_DUMP}" != "1" ]]; then
  rm -f "${REMOTE_DUMP}"
fi

echo "==> Remote database sync finished"
REMOTE_SCRIPT

echo "==> Done"
if [[ "${SKIP_REMOTE_BACKUP}" != "1" ]]; then
  echo "Remote backup saved at: ${remote_backup}"
fi
if [[ "${KEEP_LOCAL_DUMP}" == "1" ]]; then
  echo "Local dump kept at: ${local_dump}"
fi
