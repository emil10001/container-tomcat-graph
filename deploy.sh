#!/bin/sh

# build
./builddocker.sh
# tag image
docker tag -f ejf/container-tomcat-graph:v1 us.gcr.io/fodbot-1242/container-tomcat-graph-v4
# send it to docker
gcloud docker push us.gcr.io/fodbot-1242/container-tomcat-graph-v4
# run the image as a pod - name must be fewer than 24 characters
kubectl run tomcat-graph-pod --image=us.gcr.io/fodbot-1242/container-tomcat-graph-v4 --port=8080
# expose pod to load balancer
kubectl expose rc tomcat-graph-pod --type=LoadBalancer --port=8080
# describe what we have made
kubectl describe services tomcat-graph-pod
