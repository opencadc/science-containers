FROM ubuntu:24.04

# Default user to prevent running as root
ARG CARTA_USERNAME=carta-user
ARG CARTA_USER_UID=8675309
ARG CARTA_USER_GID=${CARTA_USER_UID}

# Create the user
RUN groupadd --gid ${CARTA_USER_GID} ${CARTA_USERNAME} \
    && useradd --uid ${CARTA_USER_UID} --gid ${CARTA_USER_GID} -m ${CARTA_USERNAME} \
    && apt update \
    && apt install -y software-properties-common \
    && add-apt-repository ppa:cartavis-team/carta \
    && apt update \
    && apt install -y carta-beta 

RUN mkdir /carta
WORKDIR /carta

ENV HOME="/carta"

EXPOSE 6901
RUN chmod -R a+rwx /carta

RUN mkdir -p /etc/carta && \
    chmod -R a+rwx /etc/carta

USER ${CARTA_USERNAME}

CMD ["carta", "--no_browser"]
