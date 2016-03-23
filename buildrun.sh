#!/bin/sh

./builddocker.sh
docker run -it --rm -p 8080:8080 ejf/container-tomcat-graph:v1