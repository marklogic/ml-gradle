This project shows the minimum setup to configure [Data Services](https://github.com/marklogic/java-client-api/wiki/Data-Services) in MarkLogic 9.0-6+. 

## Usage

To try this out, first modify gradle-local.properties and set appropriate values for mlUsername and mlPassword (the 
"admin" user will suffice):

    mlUsername=admin
    mlPassword=the admin password

Next, run the following Gradle task to deploy the example application:

```shell
./gradlew mlDeploy
```

This creates an app server suitable for Data Services - i.e. one without a rewriter. It also creates a user named 
"data-services-example-user" which demonstrates the minimum set of roles needed to invoke a Data Services endpoint. 

To generate the `org.example.HelloWorld` proxy class, run:

```shell
./gradlew generateHelloWorld
```

Take a look at `build.gradle` to see how this task is implemented using `com.marklogic.client.tools.gradle.EndpointProxiesGenTask`.

You can then test the application either by running the following Gradle task:

```shell
./gradlew -i test
```

Or by importing this project into your favorite IDE and running the tests under src/test/java. 

The invoked tests are:

1. A mock test that shows how the HelloWorld interface can be mocked so that functionality can be built on top of 
Data Services without requiring a connection to MarkLogic.
1. A test that constructs a DatabaseClient and invokes the real Data Services endpoint.

