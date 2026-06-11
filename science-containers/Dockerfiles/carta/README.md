# skaha-carta

## About

A CARTA session container for skaha based on the official [CARTA AppImage](https://github.com/CARTAvis/carta/releases). Supported versions are built as separate image tags via the `CARTA_VERSION` build argument.

## Building and Publishing

skaha images are managed in the CANFAR image registry at https://images.canfar.net

In order to push images to this registry, you need to be a publishing member of one of the projects, in this case `skaha`.  Since it is a private registry, you must login via `docker login` with what is called the harbor `CLI Secret` (Command Line Interface Secret).  The steps to do so are as follows:

1. Login to https://images.canfar.net/ with your CADC Userid/Password by pressing the `LOGIN VIA OIDC PROVIDER` button.
1. When prompted by harbor to enter an identification, type your CADC Userid or another name by which you wish to be known within the harbor registry.
1. Copy your CLI Secret to your clipboard under the `User Profile` menu item in the top right corner of the Harbor portal.
1. Log docker into harbor: `docker login images.canfar.net`.  Use the CLI Secret when prompted for a password.
1. Build and push each supported version:

```
docker buildx build \
  --platform linux/amd64 \
  --build-arg CARTA_VERSION=5.1.0 \
  --tag images.canfar.net/skaha/carta:5.1.0 \
  . \
  --push

docker buildx build \
  --platform linux/amd64 \
  --build-arg CARTA_VERSION=5.0.3 \
  --tag images.canfar.net/skaha/carta:5.0.3 \
  . \
  --push
```

After pushing, add the `carta` label to each image in the Harbor portal under `skaha/carta` to make it visible in the CANFAR science platform.
