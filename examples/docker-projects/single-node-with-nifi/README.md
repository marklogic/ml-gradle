Overview
---------

NOTE: WIP 

Project will create (in docker)

* A single MarkLogic node
* A nifi instance with the MarkLogic nar files
* A local directory (./logs) that will contain the logs of the MarkLogic container and nifi image
* A data mount (/data) in the nifi container that corresponds to the local project directory (./data)
    * This is useful when you want to read files from a processor in nifi

Admin account is
* Username: admin
* Password: admin

Prerequisites
-------------

1. docker toolkit installed
2. Java 8 installed
3. Following ports available -
* 8000-8050
* 8080
* 8443
* 10000

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

Docker Setup (including mlDeploy)
-------------
1. ./gradlew mlDockerDeploy

Docker Start
-------------
1. ./gradlew mlDockerStart

Docker Stop
-------------
1. ./gradlew mlDockerStop

Docker TearDown
-------------
1. ./gradlew mlDockerTeardown


___


Customisations
-------------
If you want to include the MarkLogic Converters rpm in the image, you can. You just need to modify the MarkLogic Dockerfile as specified in the comments of the Dockerfile itself