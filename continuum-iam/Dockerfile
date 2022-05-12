# Amazons JDK 15 alpine
FROM amazoncorretto:15-alpine

# For Java 11, Alpine linux version
#FROM adoptopenjdk/openjdk11:alpine-jre

# Refer to gradle build -> finalName
ARG VERSION=1.0-SNAPSHOT

# cd /opt/continuum
WORKDIR /opt/continuum-iam

# cp target/spring-boot-web.jar /opt/app/app.jar
COPY build/libs/continuum-iam-${VERSION}.jar lib/continuum-iam-${VERSION}.jar
COPY build/bootScripts/continuum-iam bin/continuum-iam

# This can be used instead if you need to run commands to test things inside the container start with
# docker build -t continuum-iam:1.0-SNAPSHOT.
# docker run --privileged -it --rm continuum-iam:1.0-SNAPSHOT
#CMD [ "/bin/sh"]

ENTRYPOINT ["bin/continuum-iam"]

