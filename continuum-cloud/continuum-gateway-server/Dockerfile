# Amazons JDK 15 alpine
FROM amazoncorretto:15-alpine

# Refer to gradle build -> finalName
ARG VERSION=1.0-SNAPSHOT

WORKDIR /opt/continuum-gateway-server

COPY build/libs/continuum-gateway-server-${VERSION}.jar lib/continuum-gateway-server-${VERSION}.jar
COPY build/bootScripts/continuum-gateway-server bin/continuum-gateway-server

# This can be used instead if you need to run commands to test things inside the container start with
# docker build -t continuum-gateway-server:1.0-SNAPSHOT .
# docker run --privileged -it --rm continuum-gateway-server:1.0-SNAPSHOT
# bin/continuum-gateway-server --spring.profiles.active=production,disableIam,ignite
# CMD [ "/bin/sh"]

ENTRYPOINT ["bin/continuum-gateway-server"]

