Overview
---------

Project will create (in docker)

* a 3 node cluster and a happroxy load balancer
* A local directory (./logs) that will contain the logs of the MarkLogic containers
* haproxy config to load balance 
    * all 3 nodes on the application REST API port (8003)
    * all 3 nodes on the qconsole port (8000)
    * all 3 nodes on the admin port (8001)
    * all 3 nodes on the manage port (8002)

Admin account is
* Username: admin
* Password: admin

Prerequisites
-------------

1. docker toolkit installed
2. Java 8 installed
3. Following ports available -
* 8000-8003
* 18000-18010
* 28000-28010
* 38000-38010


Installation steps (once off)
-----------------------------
1. Download MarkLogic-9.0-5.x86_64.rpm (or any other MarkLogic v9 rpm) and copy it to src/main/docker/marklogic
2. Execute (this will download all required docker dependencies to build marklogic image)
```
    docker-compose build   
```
3. Execute (this will download all required gradle dependencies)
```
    ./gradlew build 
```

___


Cluster Setup
-------------
1. ./gradlew mlDockerDeploy

Cluster Start
-------------
1. ./gradlew mlDockerStart

Cluster Stop
-------------
1. ./gradlew mlDockerStop

Cluster Tear Down
----------------
1. ./gradlew mlDockerTeardown


___


Customisations
-------------
If you want to include the MarkLogic Converters rpm in the image, you can. You just need to modify the MarkLogic Dockerfile as specified in the comments of the Dockerfile itself