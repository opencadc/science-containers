#!/bin/bash
set -a
export SHELL="bash"
. /etc/profile
CPU_LIMIT="${OMP_NUM_THREADS}"
export RUNNING_INSIDE_JUPYTERLAB="TRUE"
export EXTERNAL_INSTANCE_URL="https://data.lsst.cloud"
export TMPDIR="/tmp"

# use the cadc remote butler which is connected to Storage Inventory 
export DAF_BUTLER_SERVER_GAFAELFAWR_URL=DISABLED
export DAF_BUTLER_SERVER_AUTHENTICATION=cadc
[ -f ~/.ssl/cadcproxy.pem ] && export CADC_TOKEN=$(curl -E ~/.ssl/cadcproxy.pem "https://ws-cadc.canfar.net/ac/authorize?response_type=token")

exec bash $@
