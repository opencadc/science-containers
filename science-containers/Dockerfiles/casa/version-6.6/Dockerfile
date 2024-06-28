FROM images.canfar.net/skaha/ubuntu:22.04

# setup all required env variables
ARG CASA_RELEASE
ENV CASA_RELEASE=${CASA_RELEASE}
ENV PATH /usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/opt/casa/bin

# unpack and move casa databundle to container
ADD ${CASA_RELEASE}.tar.xz /opt/

# chown because the untarred casa has wrong owner/group
##NB: commands below revert back to the pre-CASA6.4 terminology, as the version of python AND el7 (etc) is now specified everywhere in the full path.
##NB2: pipelines may be an exception. 
## (the %%-py* string removes the -py3.X part of the CASA_RELEASE variable name for the directory structure)
#RUN chown -R root:root /opt/${CASA_RELEASE%%-py*} && ln -s /opt/${CASA_RELEASE%%-py*} /opt/casa
RUN chown -R root:root /opt/${CASA_RELEASE} && ln -s /opt/${CASA_RELEASE} /opt/casa

#
## add the admit enhancement (issue #25)
RUN mkdir /opt/admit
ADD admit /opt/admit
RUN cd /opt/admit && \
    autoconf && ./configure --with-casa-root=/opt/${CASA_RELEASE}

# Allow runtime symlink creation to the casa-data-repository
# Create a dangling symlink to casa-data-repository so that after deployment
# the symlink will link to the actual casa-data-repository in storage.
RUN mkdir -p /arc/projects/casa-data-repository
RUN rm -rf /opt/${CASA_RELEASE}/data && \
    ln -s /arc/projects/casa-data-repository/ /opt/${CASA_RELEASE}/data
RUN chmod 777 /opt/${CASA_RELEASE}
RUN cd /opt/${CASA_RELEASE} && \
    REPLACED_DATA=`find lib -name __data__` && \
    chmod 777 ${REPLACED_DATA}/.. && \
    rm -rf ${REPLACED_DATA} && \
    ln -s /arc/projects/casa-data-repository/ /opt/${CASA_RELEASE}/${REPLACED_DATA}
RUN rm -rf /arc

#Add in analysisUtils package
RUN mkdir /opt/casa/analysisUtils
RUN cd /opt/casa/analysisUtils && wget ftp://ftp.cv.nrao.edu/pub/casaguides/analysis_scripts.tar && tar -xvf analysis_scripts.tar
#(if above doesn't work, can manually download the package and add as below)
#ADD ./analysis_scripts.tar /opt/casa/analysisUtils/
#NB: the analysisUtils path is added to the CASA startup file in the init.sh script
# (needs access to user's $HOME)

##NEW for CASA6.*: explicitly add in astropy
##Instructions here: https://casadocs.readthedocs.io/en/latest/notebooks/frequently-asked-questions.html
RUN /opt/${CASA_RELEASE}/bin/pip3 install astropy
#also add astroquery (astroquery.readthedocs.io/en/latest/)
RUN /opt/${CASA_RELEASE}/bin/python3 -m pip install --pre astroquery[all]



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
