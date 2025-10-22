#!/bin/bash
set -a
export SHELL="bash"
export CPU_LIMIT="${OMP_NUM_THREADS-1}"
export RUNNING_INSIDE_JUPYTERLAB="TRUE"
export EXTERNAL_INSTANCE_URL="https://data.lsst.cloud"
export DAF_BUTLER_REPOSITORY_INDEX="https://www.canfar.net/storage/arc/file/projects/LSST/cadc_repositories.yaml"
export TMPDIR="/tmp"

# use the cadc remote butler which is connected to Storage Inventory 
export DAF_BUTLER_SERVER_GAFAELFAWR_URL=DISABLED
export DAF_BUTLER_SERVER_AUTHENTICATION=cadc

# initialize the LSST path
. /etc/profile
# .  /opt/lsst/software/stack/loadLSST.bash

[ -f ~/.ssl/cadcproxy.pem ] || canfar auth login
[ -f ~/.ssl/cadcproxy.pem ] && export CADC_TOKEN=$(curl -E ~/.ssl/cadcproxy.pem "https://ws-cadc.canfar.net/ac/authorize?response_type=token")
export fireflyURLLab="$(python /skaha/launch_firefly_on_canfar.py)"
export FIREFLY_URL="${fireflyURLLab}"

# start a server that can start firefly for the user 
# [ -d /opt/csp_firefly ] && (cd /opt/csp_firefly; uvicorn main:app --port 8080 >& http_log.txt &)
exec $@
