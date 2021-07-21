This project shows an example of configuring CPF with a simple pipeline and domain, with a custom condition and action.

To try this out, you need the following installed:

1. MarkLogic 8.0-3  or higher
1. Java 1.8

Deploying this application requires Gradle, but you don't need Gradle installed, as long as you have Java 1.7 installed.

Steps to deploy and test:

1. Clone this repository locally
1. cd examples/cpf-project
1. ./gradlew mlDeploy (you can include the Gradle "-i" flag to see info-level logging to see exactly what's happening)
1. ./gradlew test

The mlDeploy task will deploy an application to MarkLogic based on the configuration files under src/main/ml-config
and with the modules under src/main/ml-modules. The test task will then run the single JUnit test under src/test/java.

The above commands assume that you're deploying to a MarkLogic instance on your localhost. If you need to point to a different host,
just do (you can run mlDepoy and test together):

    ./gradlew -PmlHost=some-other-host mlDeploy test

You can also run the test in Eclipse by running "./gradlew eclipse", and then importing this project into Eclipse. 

To tinker with the CPF config:

1. If you change a file under src/main/ml-config, run "./gradlew mlRedeployCpf" to reload the CPF configuration.
1. If you change a module under src/main/ml-modules, run "./gradlew mlLoadModules" to load the new/modified module.
You can also run "./gradlew mlWatch" to continuously load new/modified modules. 

 
