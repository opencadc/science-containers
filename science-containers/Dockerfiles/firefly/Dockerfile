FROM gradle:jdk21 AS builder

COPY . /firefly
WORKDIR /firefly/cadc-sso

SHELL ["/bin/bash", "-eo", "pipefail", "-c"]

RUN set -eux; \
    gradle build --info \
    && ls -l lib/build/libs/cadc-sso-*.jar

FROM ipac/firefly:2024.3.5
COPY --from=builder /firefly/cadc-sso/lib/build/libs/cadc-sso-*.jar /usr/local/tomcat/extensions/
COPY --from=builder /firefly/launchTomcat.sh /usr/local/tomcat/

