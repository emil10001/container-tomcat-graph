# copied then modified from:
# https://github.com/jeanblanchard/docker-tomcat

FROM jeanblanchard/java:8
MAINTAINER Jean Blanchard <jean@blanchard.io>

# Expose web port
EXPOSE 8080

# Tomcat Version
ENV TOMCAT_VERSION_MAJOR 7
ENV TOMCAT_VERSION_FULL  7.0.67

# Download and install
RUN apk add --update curl &&\
  curl -kLsS https://archive.apache.org/dist/tomcat/tomcat-${TOMCAT_VERSION_MAJOR}/v${TOMCAT_VERSION_FULL}/bin/apache-tomcat-${TOMCAT_VERSION_FULL}.tar.gz \
    | gunzip -c - | tar -xf - -C /opt &&\
  ln -s /opt/apache-tomcat-${TOMCAT_VERSION_FULL} /opt/tomcat &&\
  rm -rf /opt/tomcat/webapps/examples /opt/tomcat/webapps/docs /opt/tomcat/webapps/manager /opt/tomcat/webapps/*

# Configuration
ADD tomcat-users.xml /opt/tomcat/conf/

COPY ./build/ROOT.war /opt/tomcat/webapps/

# Set environment
ENV CATALINA_HOME /opt/tomcat

# Launch Tomcat on startup
CMD ${CATALINA_HOME}/bin/catalina.sh run