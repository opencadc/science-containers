# This Dockerfile is used to build the LSST Science Platform container
# This is currently an ALMA LINUX distro
ARG VERSION=latest_daily
FROM lsstsqre/sciplat-lab:${VERSION} as deploy

# The RSP removes the passwd and group files 
# but that makes adding software fail. Add back
# a default set of system users so we can augment the container
USER 0:0
COPY src/passwd /etc/passwd
COPY src/group /etc/group
RUN cat /var/lib/dpkg/statoverride
RUN rm /var/lib/dpkg/statoverride
RUN rm /var/lib/dpkg/lock
RUN dpkg --configure -a
RUN apt-get -f install
# Add xterm so this can be run on Desktop App 
RUN apt-get install -y libx11-dev
RUN apt-get install -y xterm
RUN apt-get install -y git-lfs
RUN apt-get install -y graphviz

# install ds9, no packages and not on lsst platform by default
#RUN curl https://ds9.si.edu/download/centos7/ds9.centos7.8.5.tar.gz | tar -zx -f - -C /usr/bin/
RUN curl https://ds9.si.edu/download/centos9x86/ds9.centos9x86.8.7b1.tar.gz | tar -zx -f - -C /usr/bin/
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

# This is just for when using this as standalong container
WORKDIR /
ENTRYPOINT ["/skaha/startup.sh"]

FROM deploy as dev
RUN echo "Adding a user to run local developement"
RUN mkdir -p /arc/home
RUN groupadd -g 1001 jkavelaars
RUN useradd -u 1001 -g 1001 -s /bin/bash -d /arc/home/jkavelaars -m jkavelaars
WORKDIR /arc/home/jkavelaars
USER jkavelaars
