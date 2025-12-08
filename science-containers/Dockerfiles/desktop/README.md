# skaha-desktop

The core of this project was forked and extended from the ConSol project: https://github.com/ConSol/docker-headless-vnc-container

Modifications include a customized desktop, enabling remote DISPLAY, disabling internal browsers.

# Building

To build the docker images for skaha-desktop, set the version number in VERSION, then:
```
make
./apply-version.sh
```

# desktop icons
For an existing desktop-app without an icon on the desktop, we can follow the steps below to add an icon which a user can use to     start up the desktop-app. 
 1. Add an icon file to software-scripts/icons/. There are two limitations:
    - the icon needs to be in .svg format, e.g. ds9-terminal.svg
    - the icon file name must only use all characters before the ':' character, i.e. not ds9-terminal:8.4.1.svg.
 2. Increment the version number in VERSION
 3. Build, test and release
