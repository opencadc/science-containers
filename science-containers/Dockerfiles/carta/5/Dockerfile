FROM ubuntu:24.04

RUN apt update && \
    apt install -y software-properties-common && \
    add-apt-repository ppa:cartavis-team/carta && \
    apt update && \
    apt install -y carta

RUN mkdir /carta
WORKDIR /carta

EXPOSE 3002

RUN chmod -R a+rwx /carta

ENV HOME=/carta
ENV CARTA_DOCKER_DEPLOYMENT=1

CMD ["carta", "--no_browser"]
