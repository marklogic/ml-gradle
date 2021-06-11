FROM centos:centos7

# Get any CentOS updates then clear the Docker cache
RUN yum -y update && yum clean all

# Install MarkLogic dependencies
RUN yum -y install glibc.i686 gdb.x86_64 redhat-lsb.x86_64 && yum clean all

# Install the initscripts package so MarkLogic starts ok
RUN yum -y install initscripts && yum clean all

# Set the Path
ENV PATH /usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/opt/MarkLogic/mlcmd/bin

# Copy the MarkLogic installer to a temp directory in the Docker image being built
COPY MarkLogic-9*.rpm /tmp/MarkLogic.rpm
# Optional - copy the converter package
##COPY MarkLogicC*.rpm /tmp/MarkLogicConverters.rpm
COPY create-cluster /usr/local/sbin
COPY init-marklogic /usr/local/sbin

# Install MarkLogic then delete the .RPM file if the install succeeded
RUN yum -y install /tmp/MarkLogic.rpm && rm /tmp/MarkLogic.rpm
# Optional - Install MarkLogic Converters then delete the .RPM file if the install succeeded
##RUN yum -y install /tmp/MarkLogicConverters.rpm && rm /tmp/MarkLogicConverters.rpm

# Expose MarkLogic Server ports
# Also expose any ports your own MarkLogic App Servers use such as
# HTTP, REST and XDBC App Servers for your applications
EXPOSE 7997 7998 7999 8000 8001 8002 8003

# Start MarkLogic from init.d script.
# Define default command (which avoids immediate shutdown)
CMD /etc/init.d/MarkLogic start && tail -f /dev/null
