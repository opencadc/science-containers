#!/bin/bash
set -euo pipefail

dtm=$(date +%Y%m%d%H%M%S)
logdir="${HOME}/.carta_logs"
mkdir -p "${logdir}"

# Default command works for local docker runs.
# When Skaha env vars are provided, align with platform launch flags.
carta_cmd=(carta --no_browser)
if [ -n "${SKAHA_TOP_LEVEL_DIR:-}" ] || [ -n "${SKAHA_PROJECTS_DIR:-}" ] || [ -n "${SKAHA_SESSION_URL_PATH:-}" ]; then
  top_dir="${SKAHA_TOP_LEVEL_DIR:-/arc}"
  start_dir="${SKAHA_PROJECTS_DIR:-/arc/projects}"
  carta_port="${CARTA_PORT:-6901}"
  idle_timeout="${CARTA_IDLE_TIMEOUT:-100000}"

  carta_cmd+=(
    "--top_level_folder=${top_dir}"
    "--enable_scripting"
    "--host=0.0.0.0"
    "--port=${carta_port}"
    "--idle_timeout=${idle_timeout}"
    "--debug_no_auth"
  )
  if [ -n "${SKAHA_SESSION_URL_PATH:-}" ]; then
    carta_cmd+=("--http_url_prefix=${SKAHA_SESSION_URL_PATH}")
  fi
  carta_cmd+=("${start_dir}")
fi

command_str="$(printf '%q ' "${carta_cmd[@]}")"
command_str="${command_str% }"
echo "start.sh: command=${command_str}"

duration_args=()
if [ -n "${PSRECORD_DURATION_SECONDS:-}" ]; then
  if ! [[ "${PSRECORD_DURATION_SECONDS}" =~ ^[0-9]+$ ]] || [ "${PSRECORD_DURATION_SECONDS}" -eq 0 ]; then
    echo "start.sh: PSRECORD_DURATION_SECONDS must be a positive integer (got: ${PSRECORD_DURATION_SECONDS})" >&2
    exit 1
  fi
  duration_args=(--duration "${PSRECORD_DURATION_SECONDS}")
  echo "start.sh: psrecord will stop after ${PSRECORD_DURATION_SECONDS}s (then write log/plot under ${logdir})" >&2
fi

psrecord "${command_str}" \
  --log "${logdir}/${dtm}_carta-backend.log" \
  --plot "${logdir}/${dtm}_carta-backend.png" \
  --include-io \
  --interval 1 \
  --include-children \
  "${duration_args[@]}"
