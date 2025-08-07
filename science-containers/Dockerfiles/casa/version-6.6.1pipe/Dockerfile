FROM images.canfar.net/skaha/ubuntu:22.04
#Note that the pipeline versions of casa use a slightly different naming 
# convention for directories than the others, so some code modifications
# are needed

# setup all required env variables
ARG CASA_RELEASE
ENV CASA_RELEASE=${CASA_RELEASE}
ENV PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/opt/casa/bin

# unpack and move casa databundle to container
ADD ${CASA_RELEASE}.tar.xz /opt/

# chown because the untarred casa has wrong owner/group
##NB: the commands below show the first area where there is different syntax
# for pipeline vs non-pipeline versions of CASA. 
## (the %%-py* string removes the -py3.X part of the CASA_RELEASE variable name for the directory structure)
#(change which command below is commented out / visible when switch from 
# pipeline to other CASA versions)
RUN chown -R root:root /opt/${CASA_RELEASE%%-py*} && ln -s /opt/${CASA_RELEASE%%-py*} /opt/casa
#RUN chown -R root:root /opt/${CASA_RELEASE} && ln -s /opt/${CASA_RELEASE} /opt/casa

#
## add the admit enhancement (issue #25)
RUN mkdir /opt/admit
ADD admit /opt/admit
#comment out for 6.6.1-pipe and 6.6.4, as it doesn't work there
#RUN cd /opt/admit && \
#    autoconf && ./configure --with-casa-root=/opt/${CASA_RELEASE}

# Allow runtime symlink creation to the casa-data-repository
# Create a dangling symlink to casa-data-repository so that after deployment
# the symlink will link to the actual casa-data-repository in storage.
#RUN mkdir -p /arc/projects/casa-data-repository && \
#    rm -rf /opt/${CASA_RELEASE}/data && \
#    ln -s /arc/projects/casa-data-repository/ /opt/${CASA_RELEASE}/data && \
#    chmod 777 /opt/${CASA_RELEASE} && \
#    cd /opt/${CASA_RELEASE} && \
#    REPLACED_DATA=`find lib -name __data__` && \
#    chmod 777 ${REPLACED_DATA}/.. && \
#    rm -rf ${REPLACED_DATA} && \
#    ln -s /arc/projects/casa-data-repository/ /opt/${CASA_RELEASE}/${REPLACED_DATA} && \
#    rm -rf /arc
RUN mkdir -p /arc/projects/casa-data-repository && \
    rm -rf /opt/${CASA_RELEASE%%-py*}/data && \
    ln -s /arc/projects/casa-data-repository/ /opt/${CASA_RELEASE%%-py*}/data && \
    chmod 777 /opt/${CASA_RELEASE%%-py*} && \
    cd /opt/${CASA_RELEASE%%-py*} && \
    REPLACED_DATA=`find lib -name __data__` && \
    chmod 777 ${REPLACED_DATA}/.. && \
    rm -rf ${REPLACED_DATA} && \
    ln -s /arc/projects/casa-data-repository/ /opt/${CASA_RELEASE%%-py*}/${REPLACED_DATA} && \
    rm -rf /arc

#For CASA6.6.4+, must create a casa config file which links to the leapsecond database.
COPY casasiteconfig.py /opt/casa

#Add in analysisUtils package
RUN mkdir /opt/casa/analysisUtils && \
    cd /opt/casa/analysisUtils && wget ftp://ftp.cv.nrao.edu/pub/casaguides/analysis_scripts.tar && tar -xvf analysis_scripts.tar
#(if above doesn't work, can manually download the package and add as below)
#ADD ./analysis_scripts.tar /opt/casa/analysisUtils/
#NB: the analysisUtils path is added to the CASA startup file in the init.sh script
# (needs access to user's $HOME)

##NEW for CASA6.*: explicitly add in astropy
##Instructions here: https://casadocs.readthedocs.io/en/latest/notebooks/frequently-asked-questions.html
RUN /opt/${CASA_RELEASE%%-py*}/bin/pip3 install astropy && \
#also add astroquery (astroquery.readthedocs.io/en/latest/)
    /opt/${CASA_RELEASE%%-py*}/bin/python3 -m pip install --pre astroquery[all]


RUN mkdir /skaha
ADD init.sh /skaha/

# generate missing dbus uuid (issue #47)
RUN dbus-uuidgen --ensure

ADD nsswitch.conf /etc/

WORKDIR /opt
COPY extract-casaviewer.sh .
#below suggested by Seb to help with viewer script & variables, doesn't work
# here for some reason, and generally hasn't been effective
#ENV PYTHONPATH /opt/${CASA_RELEASE%%-py*}/lib/py/lib/python3.8/site-packages
RUN bash extract-casaviewer.sh && rm extract-casaviewer.sh

RUN chmod 777 -R /opt/squashfs-root /opt/squashfs-root/usr /opt/squashfs-root/usr/*

CMD [ "/skaha/init.sh" ]
