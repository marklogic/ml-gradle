This project shows an example of using the marklogic-unit-test framework to run server-side tests within MarkLogic.

## Enabling marklogic-unit-test in an ml-gradle project 

Using marklogic-unit-test requires two additions to the build.gradle file, as described below.

First, ml-gradle includes an "mlUnitTest" task, which depends on the marklogic-unit-test-client JAR file. ml-gradle does not
include this by default (not every ml-gradle user will use marklogic-unit-test), so it must be added to the buildscript:

    buildscript {
      repositories {
        mavenCentral()
      }
      dependencies {
        classpath "com.marklogic:marklogic-unit-test-client:1.1.0"
      }
    }

Next, the marklogic-unit-test framework is depended on and installed as an "mlBundle" dependency (the "mlBundle" configuration
is a feature of ml-gradle for depending on packages of MarkLogic modules):

    repositories {
      mavenCentral()
    }
      
    dependencies {
      mlBundle "com.marklogic:marklogic-unit-test-modules:1.1.0"
    }

## Running unit tests

With the above additions in place, the "mlUnitTest" task can be run. This task will use the value of mlTestRestPort to 
determine which MarkLogic app server to connect to - see below for how to customize this. 

First, deploy the application:

    ./gradlew mlDeploy
    
This will deploy the application along with the marklogic-unit-test modules.

Then, run the tests:

    ./gradlew mlUnitTest

Two tests are run, and one should fail, so you can see what a failed test looks like. 

This project includes the Gradle Java plugin, which allows you to run tests under src/test/java. This project includes
an example of a test that invokes each marklogic-unit-test module separately - you can try it like this:

    ./gradlew test

Again, two tests will run, and one will intentionally fail. The test can be run in an IDE as well, allowing
you to take advantage of your IDE's support for JUnit tests. The test depends on the marklogic-junit5 library from the
marklogic-unit-test project. This library provides support for writing JUnit 5 tests, including running all of your 
marklogic-unit-test modules. See [the marklogic-junit5 project](https://github.com/marklogic-community/marklogic-unit-test/tree/master/marklogic-junit5)
for more information.

You can also access the marklogic-unit-test REST endpoints directly:

- List the tests - http://localhost:8015/v1/resources/marklogic-unit-test
- Run a test suite - http://localhost:8015/v1/resources/marklogic-unit-test?rs:func=run&rs:suite=My%20Tests

And you can run the original UI test runner by going to:

- http://localhost:8015/test/default.xqy

## Configuring which server mlUnitTest connects to 

Prior to ml-gradle 3.8.1, the mlUnitTest task will connect to mlTestRestPort if it's set, else mlRestPort. 

Starting in release 3.8.1, you can configure which REST API server mlUnitTest will connect to. The mlUnitTest task now
exposes a property of type [DatabaseClientConfig](https://github.com/marklogic-community/ml-javaclient-util/blob/master/src/main/java/com/marklogic/client/ext/DatabaseClientConfig.java). 
You can configure the properties of this object, and mlUnitTest will use it for creating a connection to MarkLogic. 

Below is an example of configuring the mlUnitTest task in build.gradle - note that you need to configure every property necessary for the type of connection you want, as 
none of the properties of the DatabaseClientConfig have any default value:

```
ext {
  mlUnitTest.databaseClientConfig.host = mlHost
  mlUnitTest.databaseClientConfig.port = 8880 // probably a port that differs from mlRestPort and mlTestRestPort
  mlUnitTest.databaseClientConfig.username = mlUsername
  mlUnitTest.databaseClientConfig.password = mlPassword
  // Other properties that can be set
  // mlUnitTest.databaseClientConfig.securityContextType
  // mlUnitTest.databaseClientConfig.database
  // mlUnitTest.databaseClientConfig.sslContext
  // mlUnitTest.databaseClientConfig.sslHostnameVerifier
  // mlUnitTest.databaseClientConfig.certFile
  // mlUnitTest.databaseClientConfig.certPassword 
  // mlUnitTest.databaseClientConfig.externalName
  // mlUnitTest.databaseClientConfig.trustManager
}
```
