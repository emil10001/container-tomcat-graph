#!/bin/sh

./gradlew war
docker build -t ejf/container-tomcat-graph:v1 .
docker run -it --rm -p 8080:8080 ejf/container-tomcat-graph:v1