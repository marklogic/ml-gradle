FROM "apache/nifi:1.7.0"

USER root
ADD  https://raw.githubusercontent.com/marklogic/nifi-nars/master/nifi-marklogic-nar-1.8.0-SNAPSHOT.nar /opt/nifi/nifi-1.7.0/lib/
ADD  https://raw.githubusercontent.com/marklogic/nifi-nars/master/nifi-marklogic-services-api-nar-1.8.0-SNAPSHOT.nar /opt/nifi/nifi-1.7.0/lib/
RUN  chown nifi:nifi /opt/nifi/nifi-1.7.0/lib/nifi-marklogic*

RUN  mkdir /data
RUN  chown nifi:nifi /data

USER nifi
