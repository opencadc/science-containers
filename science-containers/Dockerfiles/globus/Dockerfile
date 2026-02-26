# Globus Connect Personal for CANFAR/Skaha
# Enables file transfers between CANFAR and other Globus endpoints
# Contributed session with web terminal (ttyd) on port 5000
FROM python:3.11-slim-bookworm

# Install system dependencies
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    wget \
    ca-certificates \
    procps \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Install ttyd (web-based terminal) from GitHub releases
# ttyd is not available in Debian bookworm repos
RUN ARCH=$(dpkg --print-architecture) && \
    if [ "$ARCH" = "amd64" ]; then TTYD_ARCH="x86_64"; else TTYD_ARCH="$ARCH"; fi && \
    wget -q "https://github.com/tsl0922/ttyd/releases/download/1.7.7/ttyd.${TTYD_ARCH}" -O /usr/local/bin/ttyd && \
    chmod +x /usr/local/bin/ttyd

# Install Globus CLI (for authentication and endpoint management)
RUN pip install --no-cache-dir globus-cli

# Download and install Globus Connect Personal
RUN wget -q https://downloads.globus.org/globus-connect-personal/linux/stable/globusconnectpersonal-latest.tgz && \
    tar xzf globusconnectpersonal-latest.tgz && \
    mv globusconnectpersonal-*/ /opt/globusconnectpersonal && \
    rm globusconnectpersonal-latest.tgz && \
    chmod +x /opt/globusconnectpersonal/globusconnectpersonal

# Create skaha directory and copy startup script
# Set permissions to 755 (rwxr-xr-x) to ensure executable by any user
RUN mkdir -p /skaha && chmod 755 /skaha
COPY skaha/startup.sh /skaha/startup.sh
RUN chmod 755 /skaha/startup.sh && \
    chown -R root:root /skaha

# Environment variables
ENV GLOBUS_HOME=/opt/globusconnectpersonal

# Expose port 5000 for web terminal (required by CANFAR contributed sessions)
EXPOSE 5000

# Set working directory
WORKDIR /home

# Skaha labels for CANFAR platform - contributed session type
LABEL ca.nrc.cadc.skaha.type="contributed" \
      ca.nrc.cadc.skaha.description="Globus Connect Personal for file transfers"

# Entry point via skaha startup script
ENTRYPOINT ["/skaha/startup.sh"]
