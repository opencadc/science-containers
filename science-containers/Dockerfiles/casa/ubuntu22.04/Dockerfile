FROM ubuntu:22.04

#This Dockerfile creates a local version of the Ubuntu 22.04 OS, along with
# standard software/libraries that CASA versions need to run.  This container
# is stored on harbor for to assist with long term stability


RUN apt update
RUN apt upgrade -y

RUN apt install -y libsm6 libxi6 libxrender1 libxrandr2 \
        libxfixes3 libxcursor1 libxinerama1 fontconfig \
        imagemagick xterm
RUN apt install -y perl autoconf autoconf graphviz xz-utils \
        gfortran libcanberra-gtk-module libsnl-dev
RUN apt install -y libxslt1.1 xauth xorg xvfb dbus

RUN apt install -y libfuse2 

RUN apt install -y gcc libgsl-dev libfftw3-dev

RUN apt install -y sssd acl

RUN apt install -y tcsh

RUN apt install -y wget



