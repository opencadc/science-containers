# CARTA beta (5.0 release candidate)

## About

A CARTA 5.0 (beta) session container for skaha based on CARTA-remote (https://github.com/CARTAvis).

This Docker image is based on the current LTS (Long Term Support) version of Ubuntu.  It includes the CARTA-beta packages from the CARTA repository.  See the [CARTAVis download](https://cartavis.org/#download) page (`v5.0-beta.1` section) for the latest version of the CARTA software.

A wrapper script, `skaha-carta`, is added to the container that calls the CARTA binary `carta`.

## Building

```sh
docker build -t images.canfar.net/skaha/carta:5.0-beta -f Dockerfile .
```

## Publishing to the image registry

skaha images are managed in the CANFAR image registry at https://images.canfar.net

In order to push images to this registry, you need to be a publishing member of one of the projects, in this case cirada.  Since it is a private registry, you must login via `docker login` with what is called the harbor `CLI Secret` (Command Line Interface Secret).  The steps to do so are as follows:

1. Login to https://images.canfar.net/ with your CADC Userid/Password by pressing the `LOGIN VIA OIDC PROVIDER` button.
1. When prompted by harbor to enter an identification, type your CADC Userid or another name by which you wish to be known within the harbor registry.
1. Copy your CLI Secret to your clipboard under the `User Profile` menu item in the top right corner of the Harbor portal.
1. Log docker into harbor by typing `docker login <CADC Userid>`.  Use the CLI Secret in your clipboard when prompted for a password.
1. Push the image to harbor:  `docker push images.canfar.net/carta/skaha-carta:4.0`
