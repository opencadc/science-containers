# skaha-carta-psrecord

## About

A CARTA 5.1.0 session container for skaha with resource profiling via [psrecord](https://github.com/astrofrog/psrecord). On session exit, CPU, memory, and I/O usage logs and plots are written to `~/.carta_logs/`.

This is a companion container to `skaha/carta` intended for performance diagnostics. For regular use, prefer `skaha/carta`.

## Resource Profiling Output

Logs and plots are written to `~/.carta_logs/` on session exit:

- `<timestamp>_carta-backend.log` — CPU, memory, and I/O usage over time
- `<timestamp>_carta-backend.png` — Plot of the above

### Timed diagnostics (`psrecord --duration`)

If the Skaha session does not run this image’s `CMD` (so `start.sh` never executes), you will not get `.carta_logs/` output; that is independent of duration.

When `start.sh` does run, you can force `psrecord` to stop after a fixed number of seconds so log/plot are written without manually ending the session:

- **Runtime:** `PSRECORD_DURATION_SECONDS=600` (example: 600 = 10 minutes).
- **Build-time:** pass `--build-arg PSRECORD_DURATION_SECONDS=600` so the value is baked into the image.

Example local run (macOS/Linux, map a folder with FITS as read-only data):

```
docker run --rm -e PSRECORD_DURATION_SECONDS=120 -p 3002:3002 -v "$HOME/carta-data:/data:ro" images.canfar.net/skaha/carta:5.1.0-psrecord
```

Adjust the published host port to match the port CARTA prints in the container logs.

## Building and Publishing

skaha images are managed in the CANFAR image registry at https://images.canfar.net

In order to push images to this registry, you need to be a publishing member of one of the projects, in this case `skaha`.  Since it is a private registry, you must login via `docker login` with what is called the harbor `CLI Secret` (Command Line Interface Secret).  The steps to do so are as follows:

1. Login to https://images.canfar.net/ with your CADC Userid/Password by pressing the `LOGIN VIA OIDC PROVIDER` button.
1. When prompted by harbor to enter an identification, type your CADC Userid or another name by which you wish to be known within the harbor registry.
1. Copy your CLI Secret to your clipboard under the `User Profile` menu item in the top right corner of the Harbor portal.
1. Log docker into harbor: `docker login images.canfar.net`.  Use the CLI Secret when prompted for a password.
1. Build and push the image:

```
docker buildx build \
  --platform linux/amd64 \
  --tag images.canfar.net/skaha/carta:5.1.0-psrecord \
  . \
  --push
```

To bake in a fixed duration (e.g. 600 seconds) for diagnostics, add `--build-arg PSRECORD_DURATION_SECONDS=600` and use a distinct tag (example: `5.1.0-psrecord-timed`).

After pushing, add the `carta` label to the image in the Harbor portal under `skaha/carta` to make it visible in the CANFAR science platform.
