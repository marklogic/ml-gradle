package com.marklogic.gradle.task.roxy

class SampleTestFiles {

    static String getSampleTestsXqy() {
        return """xquery version '1.0-ml';

import module namespace test = 'http://marklogic.com/roxy/test-helper' at '/test/test-helper.xqy';

declare option xdmp:mapping 'false';

test:assert-true(fn:true())
		       """
	}
	
    static String getSetupXqy() {
        return """xquery version '1.0-ml';

(: 
   This module will be run before each test in your suite.
   Here you might insert a document into the test database that each of your tests will modify
   If no test-specific setup is required, this file may be deleted.
   Each setup runs in its own transaction.
:) 
xdmp:log("Setup COMPLETE....")
		       """
	}
	
    static String getTeardownXqy() {
        return """xquery version '1.0-ml';

(:
   This module will run after each test in your suite.
   You might use this module to remove the document inserted by setup.xqy/setup.sjs
   If no suite-specific teardown is required, this file may be deleted.
:)
xdmp:log("Teardown COMPLETE....")
		       """
	}
        
    static String getSuiteSetupXqy() {
        return """xquery version '1.0-ml';

(:
   Runs once when your suite is started.
   You can use this to insert some data that will not be modified over the course of the suite's tests.
   If no suite-specific setup is required, this file may be deleted.
:)
xdmp:log("Suite Setup COMPLETE....")
               """
    }

    static String getSuiteTeardownXqy() {
        return """xquery version '1.0-ml';

(:
   Runs once when your suite is finished, to clean up after the suite's tests.
   If no suite-specific teardown is required, this file may be deleted.
:)
xdmp:log("Suite Teardown ENDING....")
               """
    }
}