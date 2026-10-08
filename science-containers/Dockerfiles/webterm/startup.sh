#!/bin/bash

# The platform provides the persistent home.  These directories are safe to
# create on every startup, while their contents remain user-managed.
mkdir -p "${HOME}/.local/bin" "${HOME}/.cache/uv" "${HOME}/.pixi"

session_name="${skaha_sessionname:-$(hostname)}"

exec ttyd --writable --port 5000 -w "${HOME}" \
    -t titleFixed="${session_name} - CANFAR Webterm" \
    -t theme='{"background": "#282828"}' \
    -- tmux new-session -A -s canfar bash -i
