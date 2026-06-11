FROM ubuntu:24.04

ARG CARTA_VERSION=5.1.0
ENV CARTA_VERSION=${CARTA_VERSION}

RUN apt update && \
    apt install -y wget && \
    rm -rf /var/lib/apt/lists/*

# Extract the AppImage at build time so FUSE is not required at runtime (e.g. Kubernetes).
RUN mkdir -p "/opt/carta/${CARTA_VERSION}" && \
    wget -q "https://github.com/CARTAvis/carta/releases/download/v${CARTA_VERSION}/carta.AppImage.x86_64.tgz" \
      -O /tmp/carta.AppImage.tgz && \
    tar -xzf /tmp/carta.AppImage.tgz -C "/opt/carta/${CARTA_VERSION}" && \
    cd "/opt/carta/${CARTA_VERSION}" && \
    chmod +x carta-x86_64.AppImage && \
    ./carta-x86_64.AppImage --appimage-extract && \
    if [ ! -e squashfs-root/AppRun ] && [ -e squashfs-root/usr/bin/carta ]; then \
      ln -s usr/bin/carta squashfs-root/AppRun; \
    fi && \
    mv squashfs-root app && \
    chmod -R a+rx app && \
    rm carta-x86_64.AppImage /tmp/carta.AppImage.tgz

ENV CARTA_APP="/opt/carta/${CARTA_VERSION}/app/AppRun"

RUN mkdir /carta
WORKDIR /carta

EXPOSE 6901

RUN chmod -R a+rwx /carta

ENV HOME=/carta
ENV CARTA_DOCKER_DEPLOYMENT=1

COPY start.sh /carta/start.sh
RUN chmod +x /carta/start.sh

CMD ["/carta/start.sh"]
