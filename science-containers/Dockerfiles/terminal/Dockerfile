FROM ubuntu:24.04

# --- METADATA ---
LABEL maintainer="jenkinsd" \
      project="Desktop Application Terminal Emulator" \
      description="Optimized CLI environment for CANFAR Science Platform Desktop User Sessions."

# --- SHELL PERSISTENCE & SYSTEM SETTINGS ---
ENV SHELL=/bin/bash \
    STARSHIP_CONFIG=/etc/starship.toml \
    HISTSIZE=10000 \
    HISTFILESIZE=20000 \
    TERM=xterm-256color \
    DEBIAN_FRONTEND=noninteractive

# BUCKET 1: System Foundation (Pure CLI-only tools)
RUN apt update \
    && apt install -y --no-install-recommends \
    acl \
    bash \
    bash-completion \
    ca-certificates \
    curl \
    emacs-nox \
    git \
    htop \
    jq \
    openssh-client \
    python3 \
    python3-pip \
    python3-scipy \
    python3-venv \
    rclone \
    tmux \
    vim-nox \
    x11-xserver-utils \
    xterm \
 && curl -fsSL https://opencode.ai/install | bash \
 && mv /root/.opencode/bin/opencode /usr/local/bin/opencode \
 && chmod +x /usr/local/bin/opencode \
 && rm -rf /root/.opencode \
 && curl -sS https://starship.rs/install.sh | sh -s -- --yes \
 && pip3 install --break-system-packages cadcdata cadctap cadcutils canfar vos \
 && rm -rf /var/lib/apt/lists/*

# BUCKET 2: Shell Configuration, Aliases & Robust Gruvbox Theme
RUN echo 'alias py="python3"' >> /etc/bash.bashrc \
 && echo 'alias oc="opencode"' >> /etc/bash.bashrc \
 && echo 'alias ..="cd .."' >> /etc/bash.bashrc \
 && echo 'alias ...="cd ../.."' >> /etc/bash.bashrc \
 && echo 'alias ll="ls -alF"' >> /etc/bash.bashrc \
 && echo 'alias la="ls -A"' >> /etc/bash.bashrc \
 && echo 'alias l="ls -CF"' >> /etc/bash.bashrc \
 && echo 'alias rm="rm -i"' >> /etc/bash.bashrc \
 && echo 'alias cp="cp -i"' >> /etc/bash.bashrc \
 && echo 'alias mv="mv -i"' >> /etc/bash.bashrc \
 && /usr/local/bin/starship init bash --print-full-init >> /etc/bash.bashrc \
 && echo 'export HISTFILE="${HOME}/.bash_history"' >> /etc/bash.bashrc \
 && echo 'shopt -s histappend' >> /etc/bash.bashrc \
 && echo 'PROMPT_COMMAND="history -a;$PROMPT_COMMAND"' >> /etc/bash.bashrc \
 && printf '\n# Persistent user configuration shared by terminal and Webterm sessions.\n[ -r /etc/profile.d/terminal.sh ] && . /etc/profile.d/terminal.sh\n' >> /etc/bash.bashrc \
 && cat > /etc/starship.toml <<'EOF'
scan_timeout = 1000
command_timeout = 1000
add_newline = false
palette = "gruvbox"

# Username + path on line 1; prompt character on line 2 only (no git, versions, etc.)
format = """
$username\
$directory\
$line_break\
$character"""

right_format = ""

[palettes.gruvbox]
bg0 = "#282828"
yellow = "#d79921"
aqua = "#689d6a"
green = "#98971a"
red = "#cc241d"

[username]
show_always = true
style_user = "bg:yellow fg:bg0"
style_root = "bg:red fg:bg0"
format = "[ $user ]($style)"

[directory]
style = "bg:aqua fg:bg0"
format = "[ $path ]($style)"
truncation_length = 0
truncate_to_repo = false
home_symbol = "~"

[character]
success_symbol = "[ > ](bold green)"
error_symbol = "[ x ](bold red)"
EOF

COPY terminal.sh /etc/profile.d/terminal.sh
RUN chmod 644 /etc/profile.d/terminal.sh

# xset -b: disable X keyboard bell; xterm +vb: no visual bell; bellIsUrgent: no WM urgency flash
CMD ["/bin/bash", "-c", "xset -b 2>/dev/null; exec xterm -ls +vb -xrm 'XTerm*bellIsUrgent: false'"]
