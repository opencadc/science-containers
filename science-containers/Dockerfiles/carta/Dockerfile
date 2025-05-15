FROM ubuntu:24.04

RUN apt update && \
    apt install -y software-properties-common && \
    add-apt-repository ppa:cartavis-team/carta && \
    apt update && \
    apt install -y carta-beta

RUN mkdir /carta
WORKDIR /carta

ENV HOME="/carta"

EXPOSE 6901
RUN chmod -R a+rwx /carta

RUN mkdir -p /etc/carta && \
    chmod -R a+rwx /etc/carta

CMD ["carta", "--no_browser"]
