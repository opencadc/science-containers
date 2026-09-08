#!/bin/bash
set -a
export SHELL="bash"
export CPU_LIMIT="${OMP_NUM_THREADS:-1}"
export RUNNING_INSIDE_JUPYTERLAB="TRUE"
export EXTERNAL_INSTANCE_URL="https://data.lsst.cloud"
export DAF_BUTLER_REPOSITORY_INDEX="https://www.canfar.net/storage/arc/file/projects/LSST/cadc_repositories.yaml"
export TMPDIR="/tmp"

# use the cadc remote butler which is connected to Storage Inventory
export DAF_BUTLER_SERVER_GAFAELFAWR_URL=DISABLED
export DAF_BUTLER_SERVER_AUTHENTICATION=cadc

# initialize the LSST path
. /etc/profile
.  /opt/lsst/software/stack/loadLSST.bash

# Authenticate to CADC and publish the token where RSPDiscovery looks for it:
#   1) /etc/nublado/secrets/token  (preferred)
#   2) NUBLADO_TOKEN / ACCESS_TOKEN environment variables
mkdir -p /etc/nublado/secrets /etc/nublado/discovery
[ -f ~/.ssl/cadcproxy.pem ] || canfar auth login
if [ -f ~/.ssl/cadcproxy.pem ]; then
  TOKEN=$(curl -fsS -E ~/.ssl/cadcproxy.pem \
    "https://ws-cadc.canfar.net/ac/authorize?response_type=token")
  (umask 077; printf '%s\n' "$TOKEN" > /etc/nublado/secrets/token)
  export NUBLADO_TOKEN="$TOKEN"
  export ACCESS_TOKEN="$TOKEN"
  export CADC_TOKEN="$TOKEN"
fi

# Resolve CADC IVOA resource-caps into RSPDiscovery's Nublado discovery file.
# Dataset map lives on shared ARC storage (Argo CD), not in the image.
python /skaha/populate_discovery.py \
  --map /arc/projects/LSST/cadc_dataset_map.yaml \
  || echo "WARNING: failed to populate /etc/nublado/discovery/v1.json" >&2

export fireflyURLLab="$(python /skaha/launch_firefly_on_canfar.py | grep https)"
export FIREFLY_URL="${fireflyURLLab}"

# start a server that can start firefly for the user
# [ -d /opt/csp_firefly ] && (cd /opt/csp_firefly; uvicorn main:app --port 8080 >& http_log.txt &)
exec $@
