FROM gradle:jdk21 AS base

COPY . /firefly
WORKDIR /firefly/cadc-sso

SHELL ["/bin/bash", "-eo", "pipefail", "-c"]

FROM base AS builder
RUN set -eux; \
    gradle build --info \
    && ls -l lib/build/libs/cadc-sso-*.jar

# Currently, the CADC-SSO Adapter only works with the nightly build of Firefly
FROM ipac/firefly:nightly
COPY --from=builder /firefly/cadc-sso/lib/build/libs/cadc-sso-*.jar /usr/local/tomcat/webapps-ref/firefly/WEB-INF/lib/