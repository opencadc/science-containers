# syntax=docker/dockerfile:1
# Browser-based terminal for CANFAR contributed sessions.
ARG BASE_IMAGE=images.canfar.net/skaha/terminal:1.2.5
FROM ${BASE_IMAGE}

# ttyd is not packaged by Ubuntu.  Download its small static binary rather
# than changing the terminal image's package set.
RUN arch="$(dpkg --print-architecture)" \
    && case "${arch}" in amd64) ttyd_arch=x86_64 ;; arm64) ttyd_arch=aarch64 ;; *) ttyd_arch="${arch}" ;; esac \
    && curl -fsSL "https://github.com/tsl0922/ttyd/releases/download/1.7.7/ttyd.${ttyd_arch}" -o /usr/local/bin/ttyd \
    && chmod 755 /usr/local/bin/ttyd

COPY startup.sh /skaha/startup.sh
RUN chmod 755 /skaha/startup.sh

EXPOSE 5000
LABEL ca.nrc.cadc.skaha.type="contributed" \
      ca.nrc.cadc.skaha.description="CANFAR browser-based terminal"

ENTRYPOINT ["/skaha/startup.sh"]
