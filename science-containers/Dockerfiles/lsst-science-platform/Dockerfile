# This Dockerfile is used to build the LSST Science Platform container
# This is currently an ALMA LINUX distro
# BASE_IMAGE should be a digest-pinned ref (lsstsqre/sciplat-lab@sha256:...) so
# the EUPS pins probed by Make match the image Buildx actually builds FROM.
ARG BASE_IMAGE=lsstsqre/sciplat-lab:latest_daily
FROM ${BASE_IMAGE} AS deploy

# The RSP removes the passwd and group files 
# but that makes adding software fail. Add back
# a default set of system users so we can augment the container
USER 0:0
# BuildKit does not always set HOME for RUN; EUPS/loadLSST need a writable HOME.
ENV HOME=/root \
    SHELL=/bin/bash
COPY src/passwd /etc/passwd
COPY src/group /etc/group
RUN cat /var/lib/dpkg/statoverride
RUN rm /var/lib/dpkg/statoverride
RUN rm /var/lib/dpkg/lock
RUN dpkg --configure -a
RUN apt-get update
RUN apt-get -f install
RUN sed -i -e 's/http:\/\//https:\/\//g' /etc/apt/sources.list.d/debian.sources
# Add xterm so this can be run on Desktop App 
RUN apt-get install -y libx11-dev
RUN apt-get install -y xterm
RUN apt-get install -y git-lfs
RUN apt install -y graphviz
RUN apt-get clean

# install ds9, no packages and not on lsst platform by default
# RUN curl https://ds9.si.edu/download/centos7/ds9.centos7.8.5.tar.gz | tar -zx -f - -C /usr/bin/
# RUN curl https://ds9.si.edu/download/centos9x86/ds9.centos9x86.8.7b1.tar.gz | tar -zx -f - -C /usr/bin/
RUN curl https://ds9.si.edu/download/centos9x86/xpa.centos9x86.2.1.20.tar.gz | tar -zx -f - -C /usr/bin/


# Add our own MOTD
COPY src/rsp_notice /usr/local/share/jupyterlab/etc/rsp_notice

# The skaha platform expects to find 'jupyter' in the path
# but we need to activate the correct conda first.
# add a startup script that initializes the environment correctly
RUN mkdir /skaha
COPY src/startup.sh /skaha/startup.sh
RUN chmod +x /skaha/startup.sh
COPY src/launch_firefly_on_canfar.py /skaha/launch_firefly_on_canfar.py
COPY src/jupyter /usr/bin/jupyter

# Some items to make this a better experience on CANFAR
RUN /skaha/startup.sh pip install cadctap cadcdata vos canfar safir rsp-jupyter-extensions lsdb
WORKDIR /tmp
COPY src/install_cadc_lsst_package.sh /tmp/install_cadc_lsst_package.sh
RUN chmod +x /tmp/install_cadc_lsst_package.sh

# CADC-patched LSST packages from ijiraq/lsst_<package> forks.
# Each package: feature branch rebased onto that package's EUPS pin from
# lsst_distrib.table. *_VER build-args are cache keys only (from Make probe);
# the install script always reads the pin from this image.
# Install in dependency order (resources before daf_butler).
ARG FEATURE_BRANCH=cadc_datastore

ARG RESOURCES_VER
RUN echo "resources cache key=${RESOURCES_VER}" && \
    FEATURE_BRANCH="$FEATURE_BRANCH" \
    /tmp/install_cadc_lsst_package.sh resources Required

ARG DAF_BUTLER_VER
RUN echo "daf_butler cache key=${DAF_BUTLER_VER}" && \
    FEATURE_BRANCH="$FEATURE_BRANCH" \
    /tmp/install_cadc_lsst_package.sh daf_butler Optional

# This is just for when using this as standalong container
WORKDIR /
ENTRYPOINT ["/skaha/startup.sh"]

FROM deploy AS dev
RUN echo "Adding a user to run local developement"
RUN mkdir -p /arc/home
RUN groupadd -g 1001 testuser
RUN useradd -u 1001 -g 1001 -s /bin/bash -d /arc/home/testuser -m testuser
WORKDIR /arc/home/testuser
USER testuser
# Add test stuff here if needed.
