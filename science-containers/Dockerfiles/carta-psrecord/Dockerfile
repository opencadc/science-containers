FROM ubuntu:24.04

RUN apt update && \
    apt install -y software-properties-common && \
    add-apt-repository ppa:cartavis-team/carta && \
    apt update && \
    apt install -y carta python3-pip

RUN pip3 install --break-system-packages psrecord matplotlib

RUN mkdir /carta
WORKDIR /carta

EXPOSE 3002

RUN chmod -R a+rwx /carta

ENV HOME=/carta
ENV CARTA_DOCKER_DEPLOYMENT=1

COPY start.sh /carta/start.sh
RUN chmod +x /carta/start.sh

CMD ["/carta/start.sh"]
