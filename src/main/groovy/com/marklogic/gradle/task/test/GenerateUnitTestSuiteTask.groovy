/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.gradle.task.test

import com.marklogic.gradle.task.MarkLogicTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

class GenerateUnitTestSuiteTask extends MarkLogicTask {

	static String getSampleTestsXqy() {
		return """xquery version '1.0-ml';

import module namespace test = 'http://marklogic.com/test' at '/test/test-helper.xqy';

test:assert-true(fn:true()),
test:log("\$testName COMPLETE....")"""
	}

	static String getSetupXqy() {
		return """xquery version '1.0-ml';

import module namespace test = 'http://marklogic.com/test' at '/test/test-helper.xqy';

(:
   This module will be run before each test in your suite.
   Here you might insert a document into the test database that each of your tests will modify.
   If no test-specific setup is required, this file may be deleted.
   Each setup runs in its own transaction.
:)
test:log("\$testName Setup COMPLETE....")"""
	}

	static String getTeardownXqy() {
		return """xquery version '1.0-ml';

import module namespace test = 'http://marklogic.com/test' at '/test/test-helper.xqy';

(:
   This module will run after each test in your suite.
   You might use this module to remove the document inserted by the test setup module.
   If no test-specific teardown is required, this file may be deleted.
:)
test:log("\$testName Teardown COMPLETE....")"""
	}

	static String getSuiteSetupXqy() {
		return """xquery version '1.0-ml';

import module namespace test = 'http://marklogic.com/test' at '/test/test-helper.xqy';

(:
   Runs once when your suite is started.
   You can use this to insert some data that will not be modified over the course of the suite's tests.
   If no suite-specific setup is required, this file may be deleted.
:)
test:log("\$suiteName Suite Setup COMPLETE....")"""
	}

	static String getSuiteTeardownXqy() {
		return """xquery version '1.0-ml';

import module namespace test = 'http://marklogic.com/test' at '/test/test-helper.xqy';

(:
   Runs once when your suite is finished, to clean up after the suite's tests.
   If no suite-specific teardown is required, this file may be deleted.
:)
test:log("\$suiteName Suite Teardown ENDING....")"""
	}

	@Internal
	CommandLineArguments arguments

    @TaskAction
    void generateTestSuite() {
        this.arguments = new CommandLineArguments()

        def binding = ["testName":arguments.testName, "suiteName":arguments.suiteName]
        def engine = new groovy.text.SimpleTemplateEngine()

        project.file(arguments.suitePath).mkdirs()
        def sampleTestsXqyString = getSampleTestsXqy()
        processTemplateString(engine, binding, arguments.suitePath, "/" + arguments.testName + ".xqy", sampleTestsXqyString)
        def suiteSetupXqyString = getSuiteSetupXqy()
        processTemplateString(engine, binding, arguments.suitePath, "/suite-setup.xqy", suiteSetupXqyString)
        def suiteTeardownXqyString = getSuiteTeardownXqy()
        processTemplateString(engine, binding, arguments.suitePath, "/suite-teardown.xqy", suiteTeardownXqyString)
        def setupXqyString = getSetupXqy()
        processTemplateString(engine, binding, arguments.suitePath, "/setup.xqy", setupXqyString)
        def teardownXqyString = getTeardownXqy()
        processTemplateString(engine, binding, arguments.suitePath, "/teardown.xqy", teardownXqyString)

	    println "Finished generating test suite."
    }

    def processTemplateString(engine, binding, targetBaseDir, templateFilePath, templateString) {
        def template = engine.createTemplate(templateString).make(binding)
        def templateResult = template.toString()
        def targetFilepath = targetBaseDir + templateFilePath
        project.file(targetFilepath).write(templateResult)
		println "Generated test suite file: " + targetFilepath
    }

    class CommandLineArguments {
        String suitesPath = "src/test/ml-modules/root/test/suites"
        String suiteName = 'SampleTestSuite'
        String suitePath
        String testName = 'sample-tests'

        CommandLineArguments() {
	        if (project.hasProperty("suitesPath")) {
		        this.suitesPath = project.property("suitesPath")
	        }
            if (project.hasProperty('suiteName')) {
                this.suiteName = project.property('suiteName')
            }
            this.suitePath = this.suitesPath + '/' + suiteName
            if (project.hasProperty('testName')) {
                this.testName = project.property('testName')
            }

	        suitePath = suitesPath + "/" + suiteName
        }
    }
}
