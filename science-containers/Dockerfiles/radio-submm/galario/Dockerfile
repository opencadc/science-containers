FROM images.canfar.net/skaha/base:23.09

#Install galario and helpful related packages 
RUN conda install -c conda-forge galario 
RUN conda install -c conda-forge emcee 
RUN conda install -c conda-forge corner 
RUN conda install -c conda-forge matplotlib
RUN pip install uvplot
RUN pip install schwimmbad
RUN pip install mpi4py

#install xterm: needed for desktop launch
RUN apt-get update
RUN apt-get install -y xterm

#ADD nsswitch.conf /etc/
#WORKDIR /opt
