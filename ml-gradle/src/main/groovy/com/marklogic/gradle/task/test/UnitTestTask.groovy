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

import com.marklogic.client.DatabaseClient
import com.marklogic.client.ext.DatabaseClientConfig
import com.marklogic.gradle.task.MarkLogicTask
import com.marklogic.test.unit.DefaultJUnitTestReporter
import com.marklogic.test.unit.JUnitTestSuite
import com.marklogic.test.unit.TestManager
import org.apache.commons.io.FileUtils
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

/**
 * Task for running test suites found by the marklogic-unit-test framework.
 * Tries to mirror "gradle test" in a reasonable way.
 */
class UnitTestTask extends MarkLogicTask {

	@Input
	@Optional
	DatabaseClientConfig databaseClientConfig = new DatabaseClientConfig()

	@TaskAction
	void runUnitTests() {
		try {
			Class.forName("com.marklogic.test.unit.TestManager")
		} catch (Exception ex) {
			def message = "This task requires the com.marklogic:marklogic-unit-test-client library to be a buildscript dependency."
			throw new GradleException(message)
		}

		def client = createClient()

		try {
			def testManager = new TestManager(client)

			// Initially had the following block in its own method, but that caused issues since
			// marklogic-unit-test-client is a compileOnly dependency.
			def runParams = new TestManager.RunParameters()
			if (project.hasProperty("runTeardown")) {
				runParams.withRunTeardown(Boolean.parseBoolean(project.property("runTeardown")))
			}
			if (project.hasProperty("runSuiteTeardown")) {
				runParams.withRunSuiteTeardown(Boolean.parseBoolean(project.property("runSuiteTeardown")))
			}
			if (project.hasProperty("runCodeCoverage")) {
				runParams.withCalculateCoverage(Boolean.parseBoolean(project.property("runCodeCoverage")))
			}
			if (project.hasProperty("tests")) {
				runParams.withTestNames(project.property("tests").split(","))
			}

			def suites
			long start = System.currentTimeMillis()
			if (project.hasProperty("suites")) {
				def suitesToRun = project.hasProperty("suites") ? project.property("suites").split(",").toList() : null
				println "Running suites: " + suitesToRun
				suites = testManager.runSuites(suitesToRun, runParams)
			} else {
				println "Running all suites"
				suites = testManager.runAllSuites(runParams)
			}
			println "Done running suites; time: " + (System.currentTimeMillis() - start) + "ms"

			def report = new DefaultJUnitTestReporter().reportOnJUnitTestSuites(suites)
			println report

			File resultsDir = new File(getProject().getProjectDir(), "build/test-results/marklogic-unit-test")
			String resultProperty = "unitTestResultsPath"
			if (project.hasProperty(resultProperty)) {
				resultsDir = new File(project.property(resultProperty))
			}

			if (resultsDir.exists()) {
				try {
					FileUtils.deleteDirectory(resultsDir)
					println "Deleted existing results directory: " + resultsDir
				} catch (Exception e) {
					println "Unable to delete test results directory: " + resultsDir
				}
			}

			// The resultsDir may exist in case the call to delete it failed, in which case the exception is logged
			// but not rethrown. In that case, we don't need to try to make the directory.
			if (!resultsDir.exists()) {
				if (!resultsDir.mkdirs()) {
					throw new GradleException("Unable to run tests; unable to create results directory at: " + resultsDir +
						"; please ensure you have write permission to this directory")
				}
			}

			int fileCount = 0;
			boolean testsFailed = false
			for (JUnitTestSuite suite : suites) {
				if (suite.hasTestFailures()) {
					testsFailed = true
				}
				String xml = suite.getXml()
				String filename = "TEST-" + escapeFilename(suite.getName()) + ".xml"
				org.springframework.util.FileCopyUtils.copy(xml.getBytes(), new File(resultsDir, filename))
				fileCount++;
			}

			println "\n" + fileCount + " test result files were written to: " + resultsDir

			if (testsFailed) {
				throw new GradleException("There were failing tests. See the test results at: file://" + resultsDir)
			}
		} finally {
			client.release()
		}
	}

	DatabaseClient createClient() {
		def appConfig = getAppConfig()
		if (databaseClientConfig.getPort() != null && databaseClientConfig.getPort() > 0) {
			println "Constructing DatabaseClient based on settings in the databaseClientConfig task property"
			return appConfig.configuredDatabaseClientFactory.newDatabaseClient(databaseClientConfig)
		} else if (appConfig.getTestRestPort() != null) {
			println "Constructing DatabaseClient that will connect to port: " + appConfig.getTestRestPort()
			return appConfig.newTestDatabaseClient()
		}
		println "Constructing DatabaseClient that will connect to port: " + appConfig.getRestPort()
		return appConfig.newDatabaseClient()
	}

	static String escapeFilename(String filename) {
		return filename.replaceAll("(/|\\\\)", ".")
	}
}
