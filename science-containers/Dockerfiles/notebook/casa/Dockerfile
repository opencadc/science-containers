FROM images.canfar.net/skaha/astroml:latest

LABEL maintainer="CANFAR Project <support@canfar.net>"

USER root
WORKDIR /tmp


RUN pip install --no-cache \
    casaconfig \
    casatools \
    casatasks \
    casashell \
    casaplotserver \
    casatestutils \
    casatablebrowser \
    casalogger \
    casafeather \
    casampi \
    almatasks 

#Note: packages casaplotms and casaviewer are excluded because the graphical 
# interface doesn't work properly in the Notebook environment

#copy configuration file to direct CASA to run-time data
#(NB: pip installs casa in /opt/conda/lib/python3.12/site-packages)
COPY casasiteconfig.py /opt/conda/lib/python3.12/site-packages/


