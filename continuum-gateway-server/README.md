# Continuum Gateway Server
Default server for use as a Continuum Gateway


### To build docker container
First build with gradle using intellij.. Then run the following
```shell script
docker build -t continuum-gateway-server:1.0-SNAPSHOT .
```

### To run docker container
```shell script
docker run --rm -d -t -p 58503:58503 -p 58504:58504 continuum-gateway-server:1.0-SNAPSHOT --spring.profiles.active=development,disableIam,ignite

docker run --rm -d -t -p 58503:58503 -p 58504:58504 549657833675.dkr.ecr.us-east-2.amazonaws.com/continuum/development/continuum-gateway-server
```

### To see running docker containers
```shell script
docker ps
```

### Push to ECR 
```shell script

# Create repo unless already exists 
aws ecr create-repository --repository-name continuum/development/continuum-gateway-server

# Gets login to push docker images to ECR
aws ecr get-login-password --region us-east-2 | docker login --username AWS --password-stdin 549657833675.dkr.ecr.us-east-2.amazonaws.com

# build docker image to push to ECR
docker build -t 549657833675.dkr.ecr.us-east-2.amazonaws.com/continuum/development/continuum-gateway-server .

# Now push docker image to ECR
docker push 549657833675.dkr.ecr.us-east-2.amazonaws.com/continuum/development/continuum-gateway-server

# Optionally cleanup repo 
aws ecr delete-repository --repository-name continuum/development/continuum-gateway-server
```
