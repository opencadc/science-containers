#!/usr/bin/env bash
### every exit != 0 fails the script
set -e

echo "Install Xfce4 UI components"
rm -rf /var/cache/apt/archives/*
rm -rf /var/lib/apt/lists/*
apt update
apt install -y supervisor xfce4 xfce4-terminal
apt purge -y pm-utils xscreensaver*
apt clean -y
rm -rf /var/lib/apt/lists/*
