#!/bin/bash
set -a
SHELL=bash
CPU_LIMIT=${OMP_NUM_THREADS}
export RUNNING_INSIDE_JUPYTERLAB="TRUE"
export EXTERNAL_INSTANCE_URL="https://data.lsst.cloud"
export TMPDIR="/tmp"
export FIREFLY_URL="localhost:8888/firefly"
export JUPYTER_PREFER_ENV_PATH="no"
. /etc/profile
exec $@
