This project shows an example of using the ml-unit-test framework to run server-side tests within MarkLogic.

Using ml-unit-test requires two additions to the build.gradle file, as described below.

First, ml-gradle includes an "mlUnitTest" task, which depends on the ml-unit-test-client JAR file. ml-gradle does not
include this by default (not every ml-gradle user will use ml-unit-test), so it must be added to the buildscript:

    buildscript {
      repositories {
        jcenter()
      }
      dependencies {
    	  classpath "com.marklogic:ml-unit-test-client:0.9.1"
      }
    }

Next, the ml-unit-test framework is depended on and installed as an "mlRestApi" dependency

    // Needed for ml-unit-test-client dependency until it's available via jcenter()
    repositories {
      maven {
        url {"https://dl.bintray.com/rjrudin/maven/"}
      }
    }
      
    dependencies {
      mlRestApi "com.marklogic:ml-unit-test:0.9.1"
    }

With those additions in place, the "mlUnitTest" task can be run. This task will use the value of mlTestRestPort to 
determine which MarkLogic app server to connect to. 

First, deploy the application:

    gradle mlDeploy
    
This will deploy the application along with the ml-unit-test modules.

Then, run the tests:

    gradle mlUnitTest

Two tests are run, and one should fail, so you can see what a failed test looks like. 

This project includes the Gradle Java plugin, which allows you to run tests under src/test/java. This project includes
an example of a JUnit Parameterized test that invokes each ml-unit-test module separately - you can try it like this:

    gradle test

Again, two tests will run, and one will intentionally fail. The Parameterized test can be run in an IDE as well, allowing
you to take advantage of your IDE's support for JUnit tests.

You can also access the ml-unit-test REST endpoints directly:

- List the tests - http://localhost:8135/v1/resources/ml-unit-test
- Run a test suite - http://localhost:8135/v1/resources/ml-unit-test?rs:func=run&rs:suite=My%20Tests

And you can run the original UI test runner by going to:

- http://localhost:8135/test/default.xqy
