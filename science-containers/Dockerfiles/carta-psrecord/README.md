# skaha-carta-psrecord

## About

A CARTA 5.1.0 session container for skaha with resource profiling via [psrecord](https://github.com/astrofrog/psrecord). On session exit, CPU, memory, and I/O usage logs and plots are written to `~/.carta_logs/`.

This is a companion container to `skaha/carta` intended for performance diagnostics. For regular use, prefer `skaha/carta`.

## Resource Profiling Output

Logs and plots are written to `~/.carta_logs/` on session exit:

- `<timestamp>_carta-backend.log` — CPU, memory, and I/O usage over time
- `<timestamp>_carta-backend.png` — Plot of the above

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
  -t images.canfar.net/skaha/carta:5.1.0-psrecord \
  . \
  --push
```

After pushing, add the `carta` label to the image in the Harbor portal under `skaha/carta` to make it visible in the CANFAR science platform.
