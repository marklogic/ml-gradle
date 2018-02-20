This project shows an example of using the marklogic-unit-test framework to run server-side tests within MarkLogic.

Using marklogic-unit-test requires two additions to the build.gradle file:

1. ml-gradle includes an "mlUnitTest" task, which depends on the marklogic-unit-test-client JAR file. ml-gradle does not
include this by default (not every ml-gradle user will use marklogic-unit-test), so it must be added to the buildscript:

    buildscript {
      repositories {
        jcenter()
      }
      dependencies {
    	  classpath "com.marklogic:marklogic-unit-test-client:1.0"
      }
    }

1. The marklogic-unit-test framework is depended on and installed as an "mlRestApi" dependency:

    dependencies {
      mlRestApi "com.marklogic:marklogic-unit-test:1.0"
    }

With those additions in place, the "mlUnitTest" task can be run. This task will use the value of mlTestRestPort to 
determine which MarkLogic app server to connect to. 

First, deploy the application:

    gradle mlDeploy
    
This will deploy the application along with the marklogic-unit-test modules.

Then, run the tests:

    gradle mlUnitTest

Two tests are run, and one should fail, so you can see what a failed test looks like. 
