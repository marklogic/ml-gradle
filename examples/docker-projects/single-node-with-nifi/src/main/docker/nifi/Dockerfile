FROM "apache/nifi:1.7.1"

USER root
ADD  https://github.com/marklogic/nifi/releases/download/marklogic-1.7.1.1/nifi-marklogic-nar-1.7.1.1.nar /opt/nifi/nifi-1.7.0/lib/
ADD  https://github.com/marklogic/nifi/releases/download/marklogic-1.7.1.1/nifi-marklogic-services-api-nar-1.7.1.1.nar /opt/nifi/nifi-1.7.0/lib/
RUN  chown nifi:nifi /opt/nifi/nifi-1.7.0/lib/nifi-marklogic*

RUN  mkdir /data
RUN  chown nifi:nifi /data

USER nifi
