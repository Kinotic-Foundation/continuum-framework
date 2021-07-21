# Amazons JDK 15 alpine
FROM amazoncorretto:15-alpine

# Refer to gradle build -> finalName
ARG VERSION=1.0-SNAPSHOT

WORKDIR /opt/continuum-orbiter

COPY build/libs/continuum-orbiter-${VERSION}.jar lib/continuum-orbiter-${VERSION}.jar
COPY build/bootScripts/continuum-orbiter bin/continuum-orbiter

# This can be used instead if you need to run commands to test things inside the container start with
# docker build -t continuum-orbiter:1.0-SNAPSHOT.
# docker run --privileged -it --rm continuum-orbiter:1.0-SNAPSHOT
# bin/continuum-gateway-server --spring.profiles.active=production,disableIam,ignite
#CMD [ "/bin/sh"]

ENTRYPOINT ["bin/continuum-orbiter"]

