This directory contains example ml-gradle projects to demonstrate various features and common
deployment scenarios. 

To try a particular project, clone this repository, `cd` to the directory of the project you wish to try, 
and do the following:

1. Most of the projects default to a username and password of "admin". You can change this either by modifying the 
	 `mlPassword` property in the `gradle.properties` file in the project, or - if the project is using the [Gradle 
	 properties plugin](https://plugins.gradle.org/plugin/net.saliman.properties) - you can create the file 
	 `gradle-local.properties` and set both `mlUsername` and `mlPassword` in that file.
2. If the project has any ports defined in `gradle.properties`, verify that those ports are open and available on the 
	 machine that is running MarkLogic.
3. Most projects have the [Gradle wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) setup, in 
	 which case you can deploy the project to your MarkLogic instance via `./gradlew -i mlDeploy`. If the project does 
	 not have the Gradle wrapper (you can tell by checking for a "gradlew" file in the project directory), then you'll 
	 need to have [Gradle installed](https://gradle.org/install/). After installing Gradle, you can run `./gradle -i 
	 mldeploy`.

After you've deployed and examined/tested the application, you can undeploy it via:

    ./gradlew -i -Pconfirm=true mlUndeploy

Please note that these example projects are not re-tested with every ml-gradle release. If you run into a problem with 
any of them, please file an [issue with ml-gradle](https://github.com/marklogic-community/ml-gradle/wiki).

