#!/usr/bin/env bash
### every exit != 0 fails the script
set -e

echo "Install some common tools for further installation"
rm -rf /var/cache/apt/archives
rm -rf /var/lib/apt/lists/*
apt update
apt install -y vim wget net-tools locales bzip2
apt clean -y
rm -rf /var/lib/apt/lists/*

echo "generate locales für en_US.UTF-8"
locale-gen en_US.UTF-8