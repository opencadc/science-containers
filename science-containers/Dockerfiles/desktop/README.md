# Desktop User Session Image

The current Desktop User Session image is based on Ubuntu 24.04 with the XFCE desktop environment. It includes a number of common scientific applications, as well as a VNC server (TigerVNC) and a noVNC web server to allow access to the desktop environment via a web browser.

## Structure

The Dockerfile is located at `Dockerfile.ubuntu-24.xfce`. It uses multi-stage builds to optimize the final image size, including a stage to extract noVNC files.

### VNC and noVNC
The image includes TigerVNC server to provide VNC access to the desktop environment. Additionally, it includes noVNC, which allows users to access the VNC session via a web browser without needing a separate VNC client.

VNC (Virtual Network Computing) is a graphical desktop-sharing system that uses the Remote Frame Buffer protocol (RFB) to remotely control another computer.  The noVNC application is an open-source VNC client that uses HTML5 technologies to provide access to that VNC instance through a web browser.

```
User's Browser (Science Portal) <--> noVNC (websockify) <--> TigerVNC Server <--> XFCE Desktop Environment
```

Internally, SupervisorD is used to manage both the TigerVNC server and the noVNC web server processes and ensure they are running in conjunction with each other.

### Desktop Applications
The image provides _access_ to several Desktop applications, including but not limited to:
- DS9 (SAOImage DS9)
- Aladin Sky Atlas
- Topcat
- Stellarium
- CASA (various versions)
- Firefox web browser
- Gnome Terminal

Desktop Applications can be launched from the XFCE application menu or via the main Applications menu.  These applications are spawned as Kubernetes Jobs in the background, and their windows are displayed in the user's VNC session using Remote Display through X11.

## Building the Image

This Desktop User Session image is built using standard Docker build comands:
```bash
docker build -t science-containers/desktop:latest -f Dockerfile.ubuntu-24.xfce .
```

## Running the Image (Not recommended)

Kubernetes handles the details of running the Desktop User Session image as part of the Science Platform. However, for testing purposes, you can run the image locally using Docker or Docker Compose.  It has several dependencies on host volumes and environment variables to function correctly.

```yaml
   desktop:
      image: images.canfar.net/skaha/desktop:1.2.0

      # Emulate the same command that Kuberntes would run in the Science Platform
      command: /skaha-system/start-desktop.sh <Host of Skaha API>
      networks:
         - desktop

      # Expose the noVNC port so that browser access to http://localhost:6901 works
      # Expose the VNC port for direct VNC client access (optional) to ensure connectivity, but it will be limited to noVNC in normal use.
      ports:
         - "6901:6901"
         # - "5901:5901"\

      # Non-root user.
      user: "1200:1200"
      shm_size: "2gb"
      restart: unless-stopped

      environment:
         NOVNC_PORT: "6901"
         DISPLAY: ":1"
         HOME:                       /cavern/home/<user>
         skaha_hostname:             <skaha-api-host>
         skaha_username:             <user>
         skaha_sessionid:            <skaha-session-id> # Can be anything.
         MOZ_FORCE_DISABLE_E10S:     1
         SKAHA_API_VERSION:          v1
         DESKTOP_SESSION_APP_TOKEN:  my-token
    volumes:
      # For underlying user home directories and data storage
      - $PATH_TO_CAVERN:/cavern:rw

      # For launch scripts like the start-desktop.sh script
      - $PATH_TO_SKAHA_LAUNCH_SCRIPTS:/skaha-system:ro

      # For SupervisorD to write to as the user specified above
      - $PATH_TO_SUPERVISOR_LOGS:/var/log/supervisor:rw
      - $PATH_TO_SUPERVISOR_RUN:/var/run/supervisor:rw

      # To identify the user and group inside the container
      - $PATH_TO_GROUP:/etc/group:ro
      - $PATH_TO_PASSWD:/etc/passwd:ro
```

## Adding Desktop Application Icons
For an existing desktop-app without an icon on the desktop, we can follow the steps below to add an icon which a user can use to     start up the desktop-app. 
 1. Add an icon file to software-scripts/icons/. There are two limitations:
    - the icon needs to be in .svg format, e.g. ds9-terminal.svg
    - the icon file name must only use all characters before the ':' character, i.e. not ds9-terminal:8.4.1.svg.
 2. Increment the version number in VERSION
 3. Build, test and release
