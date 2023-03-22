# Continuum Gateway Server
Default server for use as a Continuum Gateway


### To build docker container
```shell script
gradle continuum-gateway-server:bootBuildImage
```

### To run docker container
```shell script
docker run --rm -d -t -p 58503:58503 -p 58504:58504 kinotic/continuum-gateway-server:latest --spring.profiles.active=development
```

### To see running docker containers
```shell script
docker ps
```

