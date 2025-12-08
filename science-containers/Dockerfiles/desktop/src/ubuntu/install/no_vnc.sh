#!/usr/bin/env bash
### every exit != 0 fails the script
set -e
set -u

echo "Install noVNC dependencies"
apt-get install -y python3-numpy #used for websockify/novnc
apt-get clean -y

echo "Install noVNC - HTML5 based VNC viewer"
mkdir -p $NO_VNC_HOME/utils/websockify
wget -qO- https://github.com/novnc/noVNC/archive/v1.6.0.tar.gz | tar xz --strip 1 -C $NO_VNC_HOME
# use older version of websockify to prevent hanging connections on offline containers, see https://github.com/ConSol/docker-headless-vnc-container/issues/50
# wget -qO- https://github.com/novnc/websockify/archive/v0.6.1.tar.gz | tar xz --strip 1 -C $NO_VNC_HOME/utils/websockify
wget -qO- https://github.com/novnc/websockify/archive/v0.13.0.tar.gz | tar xz --strip 1 -C $NO_VNC_HOME/utils/websockify

if [ -d "$NO_VNC_HOME/utils" ]; then
    chmod +x -v $NO_VNC_HOME/utils/*.sh
fi

## create index.html to forward automatically to `vnc_lite.html`
ln -s $NO_VNC_HOME/vnc_lite.html $NO_VNC_HOME/index.html