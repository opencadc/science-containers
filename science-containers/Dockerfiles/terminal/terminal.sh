#!/bin/bash
# Loaded before the user's ~/.bashrc by /etc/bash.bashrc.
# The home directory itself is provisioned and mounted by Skaha/Cavern.
if [ -n "${HOME:-}" ]; then
    case ":${PATH}:" in
        *":${HOME}/.local/bin:"*) ;;
        *) export PATH="${HOME}/.local/bin:${PATH}" ;;
    esac

    mkdir -p "${HOME}/.config" "${HOME}/.local/bin" "${HOME}/.ssh" 2>/dev/null || true
    chmod 700 "${HOME}/.ssh" 2>/dev/null || true
fi
