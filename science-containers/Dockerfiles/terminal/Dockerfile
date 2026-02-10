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
    bash \
    bash-completion \
    ca-certificates \
    curl \
    emacs-nox \
    fonts-hack \
    git \
    htop \
    jq \
    locales \
    python3 \
    python3-pip \
    python3-venv \
    rclone \
    tmux \
    vim-nox \
    xterm \
 && curl -fsSL https://opencode.ai/install | bash \
 && mv /root/.opencode/bin/opencode /usr/local/bin/opencode \
 && chmod +x /usr/local/bin/opencode \
 && rm -rf /root/.opencode \
 && curl -sS https://starship.rs/install.sh | sh -s -- --yes \
 && pip3 install --break-system-packages cadcdata cadctap cadcutils canfar vos \
 && rm -rf /var/lib/apt/lists/*

# BUCKET 2: Locale Settings for UTF-8 Support.  This will ensure xterm and other terminal emulators can properly display UTF-8 characters, which is essential for
# applications like the canfar client and overall terminal aesthetics.
RUN locale-gen en_US.UTF-8 \
    && update-locale LANG=en_US.UTF-8 LC_CTYPE=en_US.UTF-8

# BUCKET 3: Shell Configuration, Aliases & Robust Gruvbox Theme
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
 && printf 'scan_timeout = 1000\ncommand_timeout = 1000\npalette = "gruvbox"\n\n[palettes.gruvbox]\nbg0 = "#282828"\nyellow = "#d79921"\naqua = "#689d6a"\ngreen = "#98971a"\nred = "#cc241d"\n\n[username]\nshow_always = true\nstyle_user = "bg:yellow fg:bg0"\nstyle_root = "bg:red fg:bg0"\nformat = "[ $user ]($style)"\n\n[directory]\nstyle = "bg:aqua fg:bg0"\nformat = "[ $path ]($style)"\ntruncation_length = 0\ntruncate_to_repo = false\n\n[git_branch]\nsymbol = "git:"\nstyle = "bg:green fg:bg0"\nformat = "[ $symbol$branch ]($style)"\n\n[git_status]\ndisabled = true\n\n[container]\ndisabled = true\n\n[character]\nsuccess_symbol = "[ > ](bold green)"\nerror_symbol = "[ x ](bold red)"\n' > /etc/starship.toml

CMD ["/bin/bash", "-c", "exec xterm -ls"]
