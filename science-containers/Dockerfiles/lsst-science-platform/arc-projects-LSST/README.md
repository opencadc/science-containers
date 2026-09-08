# ARC project files for LSST on CANFAR

Files in this directory are **not** baked into the LSST science-platform
container image. They are the source of truth for configuration that must
live on shared ARC storage at:

```text
/arc/projects/LSST/
```

Session startup scripts (for example `startup.sh` in the sciplat image)
read these paths at runtime so operators can update service mappings
without rebuilding images.

## Files

| File in this directory | Deployed path | Used by |
| --- | --- | --- |
| `cadc_dataset_map.yaml` | `/arc/projects/LSST/cadc_dataset_map.yaml` | `populate_discovery.py` → `/etc/nublado/discovery/v1.json` for `lsst.rsp.RSPDiscovery` |
| `cadc_repositories.yaml` | `/arc/projects/LSST/cadc_repositories.yaml` | `DAF_BUTLER_REPOSITORY_INDEX` in `startup.sh` (also at `https://www.canfar.net/storage/arc/file/projects/LSST/cadc_repositories.yaml`) |

## Argo CD deployment

These files should be synced by the platform Argo CD configuration in
[opencadc/science-platform](https://github.com/opencadc/science-platform)
(or the LSST project’s ARC provisioning workflow), **not** by the Docker
build for this image.

Recommended pattern:

1. Keep the canonical copies in this directory (this repo).
2. Add an Argo CD / GitOps sync that publishes them to
   `/arc/projects/LSST/` on the CANFAR storage backend used by Skaha
   sessions.
3. After changing `cadc_dataset_map.yaml`, new notebook sessions pick up
   the update on next start (discovery JSON is regenerated each startup).
4. After changing `cadc_repositories.yaml`, new sessions see updated Butler
   labels via `DAF_BUTLER_REPOSITORY_INDEX`.

Do **not** `COPY` these files into the Dockerfile. The container only
ships `populate_discovery.py` and env wiring that expect the ARC paths.

## Local testing

```bash
python science-containers/Dockerfiles/lsst-science-platform/src/populate_discovery.py \
  --map science-containers/Dockerfiles/lsst-science-platform/arc-projects-LSST/cadc_dataset_map.yaml \
  --dry-run
```

## Dataset map notes

- Dataset labels (`dp1`, `dp2`, `dp02`, `prompt`, …) are arguments to
  `RSPDiscovery("dp1")`.
- Service values may be:
  - a CADC IVOID from
    [resource-caps](https://ws.cadc-ccda.hia-iha.nrc-cnrc.gc.ca/reg/resource-caps)
  - a literal `https://...` URL (Rubin mirrors)
  - a `{url, versions}` dict
- `dp1` / `dp2`: CANFAR YouCAT, GMS, SIA, SODA cutout, DataLink, plus
  Butler configs on `ws-uv.canfar.net`. HiPS points at Rubin.
- `dp02` / `prompt`: discovery entries that point at `data.lsst.cloud`.

### Dual-token authentication

`RSPDiscovery` sends **one** bearer token to every service URL in a dataset.
CANFAR (CADC) and Rubin (Gafaelfawr) tokens are not interchangeable.

| Dataset | Default session token (CADC) | Rubin token required |
| --- | --- | --- |
| `dp1`, `dp2` | tap, gms, sia, cutout, datalink | hips (Rubin URL) |
| `dp02`, `prompt` | will not work | all services |

```python
import os
from lsst.rsp import RSPDiscovery

# CANFAR services (session token from /etc/nublado/secrets/token)
canfar = RSPDiscovery("dp2")
tap = canfar.get_tap_client()

# Rubin-mirrored discovery (explicit Rubin token)
rubin = RSPDiscovery("dp02", token=os.environ["RUBIN_TOKEN"])
hips_url = rubin.get_service_url("hips")
```

Create Rubin tokens via the RSP token UI:
https://rsp.lsst.io/guides/auth/creating-user-tokens.html

## Butler repository index notes

- Keys are Butler repository labels (`dp1`, `canfar-dp1`, `rubin-dp1`, …).
- Values are Butler configuration YAML URLs for CANFAR or Rubin endpoints.
