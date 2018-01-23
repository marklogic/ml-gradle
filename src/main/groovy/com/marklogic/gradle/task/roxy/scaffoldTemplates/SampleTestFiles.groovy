package com.marklogic.gradle.task.roxy

class SampleTestFiles {

    static String getSampleTestsXqy() {
        return """
xquery version '1.0-ml';

import module namespace c = 'http://marklogic.com/roxy/test-config' at '/test/test-config.xqy';
import module namespace test = 'http://marklogic.com/roxy/test-helper' at '/test/test-helper.xqy';
import module namespace sec= 'http://marklogic.com/xdmp/security' at  '/MarkLogic/security.xqy';
import module namespace cfg = 'http://thieme.com/synone/config' at '/app/config/config.xqy';


declare option xdmp:mapping 'false';

(: using global variable from app/config/config.xqy :)

declare variable \\\$admin-user as xs:string := \\\$cfg:SEARCH-USER;
declare variable \\\$admin-password as xs:string := \\\$cfg:SEARCH-PWD;


xdmp:log('\$testName Tests BEGINNING.....')
,
(: using trace API - you can turn activate and deactivate this in the admin console :)
xdmp:trace('ps-\$testName','Running in database ' || xdmp:database-name(xdmp:database()))
,
xdmp:trace('ps-\$testName','admin user =' || \\\$admin-user )

,

(: ***************************************************************

Working with Roxy Unit Testing 

Debugging: The first case to debug is where you have coded invalid or bad
xquery. You may or may not be notified of this in the web interface.

1. it is always good to pack log/trace statements into your tests.
2. Keep a tail on the ML server log at all times - sometimes setup/teardown
   will just fail without saying why (invalid xqy syntax). You will see this in the log.
3. You may want to look at the state of the test db after the test has failed 
   but by default it cleans up everything. So you cannot. On the test
   UI disable the teardown modules by clicking away the appropriate tich
   boxes. Rerun your tests and then you can examine left over docs in 
   Query Console
4. Keep it in small chunks : Code a little, test a little.
5. There are a few different kind of assertions you can code. THey 
   are defined in /test/test-helper.xqy
6. I like the assertion function below becase as well as the condition
   you give it, you can also provide a message to output when the test fails.
   There is nothing as shit as when you have 1 test out 100 failing and 
   and you don't know which one.
7. There is huge flexibility in the tests you can write. You can insert docs
   via the REST api in suite-setup then test them in your tests.
8. Test data files should be placed in the test-data dir - this is important 
   the framework will look in this folder. See the suite-setup.xqy for reading a test
   data file from this folder.              

*********v*********************************************************:)

let \\\$uri := '/content/sample.xml'
let \\\$expected-num-entries := '3'
let \\\$expected-pid := 'PI78'
let \\\$doc := fn:doc( \\\$uri )
let \\\$num-entries :=  \\\$doc/manifest/zip-num-entries/text()
let \\\$pid := \\\$doc/manifest/@productid/fn:string()
return (
  test:assert-true( \\\$num-entries = \\\$expected-num-entries,
                  'For doc '|| \\\$uri || ' number of entries should be ' || \\\$expected-num-entries ||' but is ' || \\\$num-entries
                   ),
  test:assert-true( \\\$pid = \\\$expected-pid,
                  'For doc '|| \\\$uri || ' expected product id is ' || \\\$expected-pid || ' but is ' || \\\$pid
                   )
)
,
xdmp:log('\$testName Tests ENDING.....')
        """
    }

    static String getSetupXqy() {
        return """
xquery version '1.0-ml';

import module namespace test='http://marklogic.com/roxy/test-helper' at '/test/test-helper.xqy';


xdmp:log('\$testName Setup STARTING....')


(: some stuff in there , each setup runs in its own txn :)   

,

xdmp:log('<% print testName %> Setup COMPLETE....')        """
    }

    static String getSuiteSetupXqy() {
        return """
xquery version '1.0-ml';

import module namespace test='http://marklogic.com/roxy/test-helper' at '/test/test-helper.xqy';
import module namespace c = 'http://marklogic.com/roxy/test-config' at '/test/test-config.xqy';
import module namespace cfg = 'http://thieme.com/synone/config' at '/app/config/config.xqy';

declare variable \\\$permissions := (xdmp:permission(\\\$cfg:SYNONE-READ, 'read'),
                                  xdmp:permission(\\\$cfg:SYNONE-UPDATE, 'update') );

xdmp:log('\$suiteName Suite Setup STARTING....')

,

try {(
    (: insert sample in  db:)
    let \\\$filenames := ('sample.xml')
    for \\\$f in \\\$filenames
    let \\\$uri := '/content/' || \\\$f
    let \\\$file-to-load := test:get-test-file(\\\$f)
    let \\\$_ := xdmp:log('\$suiteName Tests Suite Setup - loading '||\\\$uri)
    return xdmp:document-insert(\\\$uri, \\\$file-to-load, \\\$permissions, 'manifests' )
)} catch  (\\\$ex) {
    xdmp:log(fn:concat('\$suiteName setup failed ', \\\$ex/error:format-string/text() ),'error')
}

,

xdmp:log('\$suiteName Suite Setup COMPLETE....')
        """
    }

    static String getSuiteTeardownXqy() {
        return """
xquery version '1.0-ml';

import module namespace cfg = 'http://thieme.com/synone/config' at '/app/config/config.xqy';

declare namespace error = 'http://marklogic.com/xdmp/error';

xdmp:log('\$suiteName Suite Teardown STARTING....')

,
try {
    let \\\$result :=
        for \\\$i at \\\$count in cts:uris()
        let \\\$_ := xdmp:log(fn:concat('\$suiteName Suite Teardown : removing test file ',\\\$count,' uri = ', \\\$i) )
        return (xdmp:document-delete(\\\$i),1)
    return xdmp:log(fn:concat('\$suiteName Suite Teardown: removed ',fn:count(\\\$result), ' test files ') )

} catch (\\\$ex) {
    xdmp:log(fn:concat('\$suiteName teardown failed ', \\\$ex/error:format-string/text() ), 'error')
}

,

xdmp:log('\$suiteName Suite Teardown ENDING....')
        """
    }
}
