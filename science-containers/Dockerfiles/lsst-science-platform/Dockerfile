# This Dockerfile is used to build the LSST Science Platform container
# This is currently an ALMA LINUX distro
ARG VERSION
FROM lsstsqre/sciplat-lab:${VERSION}

# The RSP removes the passwd and group files 
# but that makes adding software fail. Add back
# a default set of system users so we can augment the container
USER 0:0
COPY src/passwd /etc/passwd
COPY src/group /etc/group

# Add xterm so this can be run on Desktop App 
RUN apt-get install -y xterm

# install ds9, no packages and not on lsst platform by default
#RUN curl https://ds9.si.edu/download/centos7/ds9.centos7.8.5.tar.gz | tar -zx -f - -C /usr/bin/
RUN curl https://ds9.si.edu/download/centos9x86/ds9.centos9x86.8.7b1.tar.gz | tar -zx -f - -C /usr/bin/
RUN curl https://ds9.si.edu/download/centos9x86/xpa.centos9x86.2.1.20.tar.gz | tar -zx -f - -C /usr/bin/

# Add our own MOTD
COPY src/rsp_notice /usr/local/share/jupyterlab/etc/rsp_notice

# THe skaha platform expects to find 'jupyter' in the path
# but we need to activate the correct conda first.
# add a startup script that initialize the environment correctly
RUN mkdir /skaha
COPY src/startup.sh /skaha/startup.sh
COPY src/jupyter /usr/local/bin/jupyter
RUN chmod +x /skaha/startup.sh
RUN chmod +x /usr/local/bin/jupyter

# Some items to make this a better experience on CANFAR
RUN /skaha/startup.sh pip install cadctap cadcdata vos
RUN /skaha/startup.sh pip install rsp-jupyter-extensions
RUN mkdir -p /rubin/cst_repos
RUN apt-get install -y git-lfs
RUN git lfs install
RUN git clone https://github.com/lsst/tutorial-notebooks.git /rubin/cst_repos/tutorial-notebooks
RUN git clone https://github.com/lsst/tutorial-notebooks-data.git /rubin/cst_repos/tutorial-notebooks-data

# This is just for when using this as standalong container
ENTRYPOINT ["bash", "/skaha/startup.sh"]
