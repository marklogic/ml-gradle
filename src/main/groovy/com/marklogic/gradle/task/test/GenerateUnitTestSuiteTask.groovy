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

	abstract class GenerateTestSuite {
		abstract String getSampleTests();
		abstract String getSetup();
		abstract String getSetupName();
		abstract String getTeardown();
		abstract String getTeardownName();
		abstract String getSuiteSetup();
		abstract String getSuiteSetupName();
		abstract String getSuiteTeardown();
		abstract String getSuiteTeardownName();
		abstract String getExtension();
	}

	class XQueryTestSuite extends GenerateTestSuite {
		String getSampleTests() {
			return """xquery version '1.0-ml';

import module namespace test = 'http://marklogic.com/test' at '/test/test-helper.xqy';

test:success(),
test:log("\$testName COMPLETE....")"""
		}

		String getSetup() {
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

		String getSetupName() {
			return "/setup.xqy";
		}

		String getTeardown() {
			return """xquery version '1.0-ml';

import module namespace test = 'http://marklogic.com/test' at '/test/test-helper.xqy';

(:
   This module will run after each test in your suite.
   You might use this module to remove the document inserted by the test setup module.
   If no test-specific teardown is required, this file may be deleted.
:)
test:log("\$testName Teardown COMPLETE....")"""
		}

		String getTeardownName() {
			return "/teardown.xqy";
		}

		String getSuiteSetup() {
			return """xquery version '1.0-ml';

import module namespace test = 'http://marklogic.com/test' at '/test/test-helper.xqy';

(:
   Runs once when your suite is started.
   You can use this to insert some data that will not be modified over the course of the suite's tests.
   If no suite-specific setup is required, this file may be deleted.
:)
test:log("\$suiteName Suite Setup COMPLETE....")"""
		}

		String getSuiteSetupName() {
			return "/suite-setup.xqy";
		}

		String getSuiteTeardown() {
			return """xquery version '1.0-ml';

import module namespace test = 'http://marklogic.com/test' at '/test/test-helper.xqy';

(:
   Runs once when your suite is finished, to clean up after the suite's tests.
   If no suite-specific teardown is required, this file may be deleted.
:)
test:log("\$suiteName Suite Teardown ENDING....")"""
		}

		String getSuiteTeardownName() {
			return "/suite-teardown.xqy";
		}

		String getExtension() {
			return ".xqy";
		}

	}

	class JavaScriptTestSuite extends GenerateTestSuite {
		String getSampleTests() {
			return """"use strict";

const test = require('/test/test-helper.xqy');

test.success(),
test.log("\$testName COMPLETE....")"""
		}

		String getSetup() {
			return """"use strict";

const test = require('/test/test-helper.xqy');

declareUpdate();

/*
   This module will be run before each test in your suite.
   Here you might insert a document into the test database that each of your tests will modify.
   If no test-specific setup is required, this file may be deleted.
   Each setup runs in its own transaction.
*/
test.log("\$testName Setup COMPLETE....")"""
		}

		String getSetupName() {
			return "/setup.sjs";
		}

		String getTeardown() {
			return """"use strict";

const test = require('/test/test-helper.xqy');

declareUpdate();

/*
   This module will run after each test in your suite.
   You might use this module to remove the document inserted by the test setup module.
   If no test-specific teardown is required, this file may be deleted.
*/
test.log("\$testName Teardown COMPLETE....")"""
		}

		String getTeardownName() {
			return "/teardown.sjs";
		}

		String getSuiteSetup() {
			return """"use strict";

const test = require('/test/test-helper.xqy');

declareUpdate();

/*
   Runs once when your suite is started.
   You can use this to insert some data that will not be modified over the course of the suite's tests.
   If no suite-specific setup is required, this file may be deleted.
*/
test.log("\$suiteName Suite Setup COMPLETE....")"""
		}

		String getSuiteSetupName() {
			return "/suiteSetup.sjs";
		}

		String getSuiteTeardown() {
			return """"use strict";

const test = require('/test/test-helper.xqy');

declareUpdate();

/*
   Runs once when your suite is finished, to clean up after the suite's tests.
   If no suite-specific teardown is required, this file may be deleted.
*/
test.log("\$suiteName Suite Teardown ENDING....")"""
		}

		String getSuiteTeardownName() {
			return "/suiteTeardown.sjs";
		}

		String getExtension() {
			return ".sjs";
		}

	}

	@Internal
	CommandLineArguments arguments

	@TaskAction
	void generateTestSuite() {
		this.arguments = new CommandLineArguments()
		GenerateTestSuite generator = this.arguments.lang == CommandLineArguments.Language.JAVASCRIPT ? new JavaScriptTestSuite() : new XQueryTestSuite();

		def binding = ["testName":arguments.testName, "suiteName":arguments.suiteName]
		def engine = new groovy.text.SimpleTemplateEngine()

		project.file(arguments.suitePath).mkdirs()
		processTemplateString(engine, binding, arguments.suitePath, "/" + arguments.testName + generator.getExtension(), generator.getSampleTests())
		processTemplateString(engine, binding, arguments.suitePath, generator.getSuiteSetupName(), generator.getSuiteSetup())
		processTemplateString(engine, binding, arguments.suitePath, generator.getSuiteTeardownName(), generator.getSuiteTeardown())
		processTemplateString(engine, binding, arguments.suitePath, generator.getSetupName(), generator.getSetup())
		processTemplateString(engine, binding, arguments.suitePath, generator.getTeardownName(), generator.getTeardown())

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
		public enum Language { XQUERY, JAVASCRIPT }
		String suitesPath = "src/test/ml-modules/root/test/suites"
		String suiteName = 'SampleTestSuite'
		String suitePath
		String testName = 'sample-tests'
		Language lang

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
			if (project.hasProperty('language')) {
				if (project.property('language').equals('sjs')) {
					lang = Language.JAVASCRIPT
				} else {
					lang = Language.XQUERY
				}
			} else {
				lang = Language.XQUERY
			}

			suitePath = suitesPath + "/" + suiteName
		}
	}
}
