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
# This will install the necessary APT packages for a robust terminal environment, including Python and its scientific libraries, as well as tools like tmux, vim, and htop. It also installs the Starship prompt and OpenCode CLI for enhanced terminal functionality. After installation, it cleans up APT caches to reduce the image size.
# Important note:
# Python packages are installed with --break-system-packages to allow installation of packages that may conflict with the system's Python installation. This is necessary because some scientific Python packages may have dependencies that conflict with the versions provided by the base Ubuntu image. However, this should be done with caution, as it can potentially break system tools that rely on the default Python installation.
# While Debian based systems recommend using APT to install Python packages, the CADC and CANFAR client libraries are missing from APT and more up-to-date and better maintained on PyPI, so we use pip for those installations.
# This also means that all Python packages are installed through pip to ensure compatibility with dependencies.
# Ensure that the default python command points to python3.
# jenkinsd 2026.02.11
RUN apt update \
    && apt install -y --no-install-recommends \
    bash \
    bash-completion \
    ca-certificates \
    curl \
    cython3 \
    emacs-nox \
    fontconfig \
    fonts-hack \
    git \
    htop \
    jq \
    libfreetype6 \
    libxft2 \
    locales \
    python3 \
    python3-pip \
    python3-venv \
    rclone \
    tmux \
    vim-nox \
    xfonts-base \
    xterm \
 && curl -fsSL https://opencode.ai/install | bash \
 && mv /root/.opencode/bin/opencode /usr/local/bin/opencode \
 && chmod +x /usr/local/bin/opencode \
 && rm -rf /root/.opencode \
 && curl -sS https://starship.rs/install.sh | sh -s -- --yes \
 && apt clean \
 && rm -rf /var/lib/apt/lists/* \
 && rm -rf /root/.cache \
 && update-alternatives --install /usr/bin/python python /usr/bin/python3 8

# All Python packages are installed through pip to ensure compatibility with dependencies, especially for the CANFAR client libraries which are not available in APT. The --break-system-packages flag is used to allow installation of packages that may conflict with the system's Python installation, but this should be done with caution as it can potentially break system tools that rely on the default Python installation.
RUN pip3 install --break-system-packages astropy astroquery cadcdata cadctap cadcutils canfar matplotlib numpy spyder vos

# BUCKET 2: Locale Settings for UTF-8 Support.  This will ensure xterm and other terminal emulators can properly display UTF-8 characters, which is essential for
# applications like the canfar client and overall terminal aesthetics.
RUN locale-gen en_US.UTF-8 \
    && update-locale LANG=en_US.UTF-8 LC_CTYPE=en_US.UTF-8

# Should be set from the command above, but just in case, we set these environment variables to ensure that the terminal and applications within it use UTF-8 encoding by default.
ENV LC_CTYPE=en_US.UTF-8
ENV LC_ALL=en_US.UTF-8 

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
