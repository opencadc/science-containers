FROM gradle:jdk21 AS builder

COPY . /firefly
WORKDIR /firefly/cadc-sso

SHELL ["/bin/bash", "-eo", "pipefail", "-c"]

RUN set -eux; \
    gradle build --info \
    && ls -l lib/build/libs/cadc-sso-*.jar

FROM ipac/firefly:nightly
COPY --from=builder /firefly/entrypoint.py /usr/local/tomcat/
COPY --from=builder /firefly/cadc-sso/lib/build/libs/cadc-sso-*.jar /usr/local/tomcat/webapps-ref/firefly/WEB-INF/lib/

