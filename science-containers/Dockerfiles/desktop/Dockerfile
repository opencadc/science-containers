# Base Ubuntu 24.04 image.
# This image updates apt sources to use https and updates CA certificates.  It exists to workaround
# local network issues where plain http apt sources are blocked or intercepted.
# If this is not needed in your environment, you can use:
# <code>FROM ubuntu@sha256:4fdf0125919d24aec972544669dcd7d6a26a8ad7e6561c73d5549bd6db258ac2</code> (Ubuntu 24.04 LTS)
# directly in the actual Dockerfile.
# jenkinsd - 2026-01-16
#
FROM ubuntu@sha256:4fdf0125919d24aec972544669dcd7d6a26a8ad7e6561c73d5549bd6db258ac2 AS base
ENV DEBIAN_FRONTEND=noninteractive

# Update trusted CA certificates, and ensure https is used in apt sources
RUN apt-get update \
    && apt-get install --reinstall -y apt-transport-https ca-certificates \
    && update-ca-certificates \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/* \
    && sed -i "s|http:|https:|g" /etc/apt/sources.list.d/*

# Ubuntu custom base image (based from 24.04 LTS) to extract noVNC package.json
# This layer is used to avoid downloading the full noVNC source each time the main image is built.
# The package.json is used by some desktop environments to identify the noVNC version.
# jenkinsd - 2026-01-16
#
FROM base AS novnc-extractor

WORKDIR /tmp/novnc

# Use curl to download the archive to stdout, pipe to tar to extract it directly.
# Extract only the package.json file
# The 1.3.0 is used here for compatibility with Ubuntu (24.04) version
RUN apt update \
    && apt install -y curl tar \
    && curl -sL "https://github.com/novnc/noVNC/archive/v1.3.0.tar.gz" | tar -xzf - "noVNC-1.3.0/package.json" \
    && mv "noVNC-1.3.0/package.json" ./package.json

# Ubuntu custom base image (based from 24.04 LTS) as the main running image
# with XFCE desktop environment, TigerVNC server, and noVNC web client.
# jenkinsd - 2026-01-16
#
FROM base AS production

ENV REFRESHED_AT=2026-01-26

# Ensure noninteractive apt operations (keep in frontend)
ENV DEBIAN_FRONTEND=noninteractive

# The port that the VNC Server listens on
ENV VNC_PORT=5901

# The Port that noVNC web server listens on.  This is used by browser clients to connect to the VNC session from the Science Portal.
ENV NOVNC_PORT=6901

ENV TERM=xterm

# Where the noVNC installation is located.  Used to determine the web page (HTML) for the browser client.
ENV NOVNC_HOME=/usr/share/novnc

# VNC server settings
ENV VNC_COL_DEPTH=24
ENV VNC_RESOLUTION=1920x1200
ENV VNC_VIEW_ONLY=false

# Use a headless XDG config and desktop directory to avoid needing a real user home
ENV XDG_CONFIG_HOME=/headless
ENV XDG_DESKTOP_DIR=/headless/Desktop
ENV MOZ_FORCE_DISABLE_E10S=1

# Install system dependencies
# The software-porperties-common is required to add the Firefox PPA below
#
# This installation includes:
# - Install XFCE desktop and VNC/noVNC packages
# - Firefox ESR from Mozilla PPA (Mainstream Firefox requires SNAP)
# - SVG Support to display icons
# - gedit text editor
# - jq for JSON processing in scripts
# - gnome-terminal for terminal emulator
# - XDG packages for menu management and generation
# - updated ca certificates
#
RUN apt-get update \
    && apt-get install -y \
    at-spi2-core \
    bzip2 \
    curl \
    dconf-cli \
    dbus-user-session \
    dbus-x11 \
    emacs \
    gedit \
    gettext \
    gnome-terminal \
    gpg-agent \
    iproute2 \
    jq \
    librsvg2-common \
    locales \
    mailcap \
    menu-xdg \
    net-tools \ 
    novnc \
    pm-utils \
    python3-numpy \
    python3-websockify \
    software-properties-common \
    supervisor \
    tigervnc-common \
    tigervnc-standalone-server \
    tigervnc-tools \
    twm \
    vim \
    websockify \
    wget \
    x11-xserver-utils \
    xdg-utils \
    xfce4 \
    xfce4-appfinder \
    xinit \
    xterm \
    --no-install-recommends

# Install latest Firefox from Mozilla PPA to avoid needing the SNAP version
RUN add-apt-repository ppa:mozillateam/ppa \
    && apt-get update \
    && apt-get install -y firefox-esr \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/* \
    && mkdir -p /usr/lib/firefox/distribution \
    && printf '{ \
  "policies": { \
    "DisableAppUpdate": true, \
    "DisableFirefoxStudies": true \
  } \
}' > /usr/lib/firefox/distribution/policies.json

# Create Firefox profile and space
RUN mkdir -p /headless/mozilla/skaha \
    && firefox-esr --headless -CreateProfile "skaha /headless/mozilla/skaha" \
    && chmod -R 777 /headless/mozilla/skaha

# Set gnome-terminal as the default terminal emulator to prevent being prompted.
RUN update-alternatives --install /usr/bin/x-terminal-emulator x-terminal-emulator /usr/bin/gnome-terminal 50   

# Out of the way working directory
WORKDIR /tmp

# Necessary for some X applications to start properly
RUN mkdir -p /tmp/.ICE-unix \
    && chown root:root /tmp/.ICE-unix \
    && chmod 1777 /tmp/.ICE-unix   

# CANFAR Specifics Below
# Add global menu configuration files
COPY conf/xfce-applications.menu /etc/xdg/menus/xfce-applications.menu
COPY conf/xfce-applications.menu /etc/xdg/menus/applications.menu

# Add desktop icon files
RUN rm -rf /headless \
    && mkdir -p /headless/.icons \
    && mkdir -p /headless/Desktop \
    && mkdir -p /headless/xfce4 \
    && mkdir -p /headless/dconf \
    && chmod 777 /headless/.icons \
    && chmod 777 /headless/Desktop \ 
    && mkdir -p /etc/xdg/menus/applications-merged \
    && chmod 777 /etc/xdg/menus/applications-merged

# create the guest account
RUN groupadd -g 30001 guest
RUN useradd -m -g guest -u 30001 guest && \
    mkdir -p /home/guest/.ssl && \
    chown -R guest:guest /home/guest/.ssl && \
    ln -s /home/guest/.ssl /headless/.ssl

# XDG Menu configuration (user directories and general config for the top left menu)
RUN mkdir -p $XDG_CONFIG_HOME/.config \
    && chmod -R 777 $XDG_CONFIG_HOME/.config
ADD ./src/common/xfce/.config/user-dirs.dirs $XDG_CONFIG_HOME/.config/
ADD ./src/common/xfce/.config/ $XDG_CONFIG_HOME/

# Add scripts that launch sofware containers
ADD software-scripts/*.sh /usr/local/bin/

# Add icons for the Desktop applications (SVG and PNG).  This is used to provide icons for the desktop links.
COPY software-scripts/icons/* /headless/.icons

# Add desktop links to those scripts
COPY software-scripts/*.desktop /usr/share/applications/

# Add a subset of software links to the desktop
COPY --chown=guest:root software-scripts/arcade-feedback.desktop /headless/Desktop/arcade-feedback.desktop
COPY --chown=guest:root software-scripts/firefox.desktop /headless/Desktop/firefox.desktop
COPY --chown=guest:root software-scripts/gedit.desktop /headless/Desktop/gedit.desktop
COPY --chown=guest:root software-scripts/kill-session.desktop /headless/Desktop/kill-session.desktop

# Cleanup default Thunar user custom actions and screensaver autostart
RUN rm /etc/xdg/Thunar/uca.xml
RUN rm -f /etc/xdg/autostart/xscreensaver.desktop

# Global VNC startup script (does not touch user HOME)
# This will be executed by TigerVNC server when a VNC session is started.
# Some notes about this script:
# - It uses dbus-launch to start a D-Bus session for the VNC session
# - It starts the XFCE4 session
# - It disables screen blanking and power saving
# - It enables access control for X11 (xhost +) to allow applications to connect to the X server
# - DPMS is not installed by default in the base image, so no need to disable that specifically
# - It sources the user's .Xresources file if it exists.  The distributed .Xresources file has some basic settings for copy and paste
#   but it also contains invalid comments that begin with # instead of !, so those are fixed before loading.
# - Uses xfconf-query to set the desktop wallpaper settings to blank (no wallpaper) for ease of viewing.
# - Users xconf-query to set gnome-terminal as the default terminal emulator to prevent being prompted.
#
# jenkinsd 2026.01.22
#
RUN mkdir -p /etc/vnc && \
    cat <<'EOF' > /etc/vnc/xstartup
#!/bin/sh
unset SESSION_MANAGER
unset DBUS_SESSION_BUS_ADDRESS

if [ -r "$HOME/.Xresources" ]; then
    sed -i 's/^#/\!/' "$HOME/.Xresources"
    xrdb "$HOME/.Xresources"
fi

eval "$(dbus-launch --sh-syntax)"

xhost +
xset s off
xset s noblank

gsettings set org.gnome.Terminal.Legacy.Settings always-check-default-terminal false

exec startxfce4

EOF

# Make the VNC xstartup script executable
RUN chmod 775 /etc/vnc/xstartup

# Supervisor setup
# SupervisorD is a daemon that can manage multiple processes.  The configuration below will start both the VNC server (TigerVNC)
# and the noVNC web server (websockify) to allow browser-based VNC access.  SupervisorD will also manage restarts of these services if they exit unexpectedly.

# SupervisorD will need to be run as the current user, so make this writable
RUN mkdir -p /etc/supervisor/conf.d \
    && chmod -R 777 /etc/supervisor/conf.d \
    && mkdir -p /var/log/supervisor /var/run/supervisor \
    && chmod -R 0775 /var/log/supervisor /var/run/supervisor

VOLUME [ "/var/log/supervisor", "/var/run/supervisor" ]

# Supervisor configuration (no VNC password, token-based noVNC configuration)
# This will start both the VNC server and the noVNC web server, proxying the $NOVNC_PORT to localhost:VNC_PORT as specified in /etc/novnc/tokens (below)
# The unforunate "SecurityTypes None" is required to allow noVNC to connect without a password, and the --I-KNOW-THIS-IS-INSECURE flag is required to allow that option
RUN cat <<'EOF' > /etc/supervisor/conf.d/supervisord.conf
[supervisord]
nodaemon=true
logfile=/var/log/supervisor/supervisord.log
pidfile=/var/run/supervisor/supervisord.pid

[program:vnc]
command=/usr/bin/tigervncserver -fg :1 -localhost no -listen tcp -cleanstale -AlwaysShared -AcceptSetDesktopSize=1 -NeverShared=0 -SecurityTypes None -depth %(ENV_VNC_COL_DEPTH)s -geometry %(ENV_VNC_RESOLUTION)s -xstartup /etc/vnc/xstartup --I-KNOW-THIS-IS-INSECURE
autorestart=unexpected
startsecs=3

[program:novnc]
command=%(ENV_NOVNC_HOME)s/utils/novnc_proxy --listen %(ENV_NOVNC_PORT)s --vnc localhost:%(ENV_VNC_PORT)s --web /usr/share/novnc
autorestart=true
EOF

# Setup NoVNC default page, and copy the extracted package.json from the novnc-extractor stage
COPY conf/novnc/index.html ${NOVNC_HOME}/index.html
COPY conf/novnc/canfar-icon.png ${NOVNC_HOME}/app/images/icons/
COPY conf/novnc/*-logo.png ${NOVNC_HOME}/app/images/
COPY --from=novnc-extractor /tmp/novnc/package.json ${NOVNC_HOME}/app/package.json
COPY --from=novnc-extractor /tmp/novnc/package.json ${NOVNC_HOME}/package.json

# Modify the default UI JavaScript to allow remote resizing, and modify the title
RUN sed -i "s/('resize',\ 'off')/\('resize',\ 'remote')/" ${NOVNC_HOME}/app/ui.js \
    && sed -i 's/document\.title\ =.*;/document\.title\ =\ "ARCADE";/' ${NOVNC_HOME}/app/ui.js \
    && sed -i 's/<title>noVNC<\/title>/<title>ARCADE<\/title>/' ${NOVNC_HOME}/index.html

EXPOSE ${NOVNC_PORT}

RUN chmod -R 2777 /headless/xfce4 \
    && chmod -R 2777 /headless/dconf

# Start SupervisorD to launch VNC and noVNC services
# Ensure any overriding scripts reproduce this command!
# @see https://github.com/opencadc/deployments/blob/main/helm/applications/skaha/launch-scripts/start-desktop.sh
CMD ["/usr/bin/supervisord", "-c", "/etc/supervisor/conf.d/supervisord.conf"]
