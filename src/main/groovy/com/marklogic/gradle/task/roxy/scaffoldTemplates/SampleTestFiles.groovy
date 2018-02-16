package com.marklogic.gradle.task.roxy

class SampleTestFiles {

    static String getSampleTestsXqy() {
        return """
xquery version '1.0-ml';

import module namespace test = 'http://marklogic.com/roxy/test-helper' at '/test/test-helper.xqy';

declare option xdmp:mapping 'false';

test:assert-true(fn:true())
		       """
	}
	
    static String getSetupXqy() {
        return """
xquery version '1.0-ml';

(: Put test-specific setup code in here. Such as putting the database in a testable state. :)
(: If no test-specific setup is required, this file may be deleted. :)
(: Each setup runs in its own transaction. :) 
xdmp:log("Setup COMPLETE....")
		       """
	}
        
    static String getSuiteSetupXqy() {
        return """
xquery version '1.0-ml';

(: Put suite-specific setup code in here. :)
(: If no suite-specific setup is required, this file may be deleted. :)
xdmp:log("Suite Setup COMPLETE....")
               """
    }

    static String getSuiteTeardownXqy() {
        return """
xquery version '1.0-ml';

(: Put suite-specific teardown code in here. :)
(: This might include database cleanup. :)
(: If no suite-specific teardown is required, this file may be deleted. :)
xdmp:log("Suite Teardown ENDING....")
               """
    }
}