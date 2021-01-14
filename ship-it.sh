#!/bin/sh

docker stop $(docker ps -aq) && docker rm $(docker ps -aq)

cd client
mvn clean install
docker build -t client .
cd ..

cd server
mvn clean install
docker build -t server .
cd ..

docker-compose up
