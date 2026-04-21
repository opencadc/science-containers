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

# Optional: set at build (`--build-arg`) or runtime (`-e`) so psrecord uses `--duration` and exits without relying on a manual session stop.
# Example timed diagnostics build: --build-arg PSRECORD_DURATION_SECONDS=600
ARG PSRECORD_DURATION_SECONDS=
ENV PSRECORD_DURATION_SECONDS=${PSRECORD_DURATION_SECONDS}

COPY start.sh /carta/start.sh
RUN chmod +x /carta/start.sh

CMD ["/carta/start.sh"]
