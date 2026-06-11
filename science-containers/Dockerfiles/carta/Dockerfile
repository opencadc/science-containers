FROM ubuntu:24.04

ARG CARTA_VERSION=5.1.0
ENV CARTA_VERSION=${CARTA_VERSION}

RUN apt update && \
    apt install -y wget libfuse2t64 && \
    rm -rf /var/lib/apt/lists/*

RUN mkdir -p "/opt/carta/${CARTA_VERSION}" && \
    wget -q "https://github.com/CARTAvis/carta/releases/download/v${CARTA_VERSION}/carta.AppImage.x86_64.tgz" \
      -O /tmp/carta.AppImage.tgz && \
    tar -xzf /tmp/carta.AppImage.tgz -C "/opt/carta/${CARTA_VERSION}" && \
    chmod +x "/opt/carta/${CARTA_VERSION}/carta-x86_64.AppImage" && \
    rm /tmp/carta.AppImage.tgz

ENV CARTA_APP="/opt/carta/${CARTA_VERSION}/carta-x86_64.AppImage"

RUN mkdir /carta
WORKDIR /carta

EXPOSE 6901

RUN chmod -R a+rwx /carta

ENV HOME=/carta
ENV CARTA_DOCKER_DEPLOYMENT=1

COPY start.sh /carta/start.sh
RUN chmod +x /carta/start.sh

CMD ["/carta/start.sh"]
