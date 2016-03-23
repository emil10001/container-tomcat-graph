#!/bin/sh

./gradlew war
cp build/libs/container-tomcat-graph-0.1.war build/ROOT.war
docker build -t ejf/container-tomcat-graph:v1 .