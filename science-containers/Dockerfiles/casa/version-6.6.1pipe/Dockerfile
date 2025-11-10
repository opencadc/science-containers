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

## add the admit enhancement (issue #25)
#ADMIT now installed after astropy as uses some of the same libraries
RUN apt-get update && apt install -y git && \
   cd /opt && git clone https://github.com/astroumd/admit && \
   cd /opt/admit && git checkout python3 && \
   /opt/${CASA_RELEASE%%-py*}/bin/pip3 install -e .
#### re-write the start-up file, since instructions want the user to manually edit some fields from the default file downloaded from git
###first command starts the file afresh, subsequent ones add lines to it
#RUN cd /opt/admit && \
#        echo '#/bin/bash' > admit_start.sh.in  && \
#        echo 'export ADMIT=/opt/admit' >> admit_start.sh.in && \
#        echo 'export CASA_ROOT=/opt/'${CASA_RELEASE} >> admit_start.sh.in && \
#        echo 'if test "x$CASA_ROOT" = "x"; then' >> admit_start.sh.in && \
#        echo '    unset CASA_ROOT' >> admit_start.sh.in && \
#        echo '    export PATH=${ADMIT}/bin:${PATH}' >> admit_start.sh.in && \
#        echo 'else' >> admit_start.sh.in && \
#        echo '    # CASA_ROOT has been deprecated as a bin by CASA' >> admit_start.sh.in && \
#        echo '    export PATH=${ADMIT}/bin:${CASA_ROOT}/bin:${CASA_ROOT}/lib/casa/bin:${CASA_ROOT}/:${PATH}' >> admit_start.sh.in && \
#        echo '    export TCL_LIBRARY=${CASA_ROOT}/share/tcl' >> admit_start.sh.in && \
#        echo '    export TK_LIBRARY=${CASA_ROOT}/share/tk' >> admit_start.sh.in && \
#        echo '    export LD_LIBRARY_PATH=${CASA_ROOT}/lib:${LD_LIBRARY_PATH}' >>admit_start.sh.in && \
#        echo 'fi' >> admit_start.sh.in && \
#        echo 'export CASA_PATH="$CASA_ROOT linux admit `hostname`"' >> admit_start.sh.in && \
#        echo 'export PYTHONPATH=${ADMIT}:${PYTHONPATH}' >> admit_start.sh.in && \
#        echo 'ln -s /bin/python3 /bin/python' >> admit_start.sh.in && \
#        bash /opt/admit/admit_start.sh.in
#(HK added last line of admit_start.sh.in to avoid an error message about 'python not found' when running admit otherwise
#HK modified last command from recommended 'source' to 'bash' for it to work here.  ('source' works when logging into the terminal for testing, but not in the build script)
#Earlier versions of CASA can install ADMIT using the lines below instead
#RUN mkdir /opt/admit
#ADD admit /opt/admit
#comment out for 6.6.1-pipe and 6.6.4, as it doesn't work there
#RUN cd /opt/admit && \
#    autoconf && ./configure --with-casa-root=/opt/${CASA_RELEASE}

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
