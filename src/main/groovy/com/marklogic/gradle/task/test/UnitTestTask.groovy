package com.marklogic.gradle.task.test

import com.marklogic.client.ext.DatabaseClientConfig
import com.marklogic.gradle.task.MarkLogicTask
import com.marklogic.test.unit.DefaultJUnitTestReporter
import com.marklogic.test.unit.JUnitTestSuite
import com.marklogic.test.unit.TestManager
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

/**
 * Task for running test suites found by the marklogic-unit-test framework.
 * Tries to mirror "gradle test" in a reasonable way.
 */
class UnitTestTask extends MarkLogicTask {

	DatabaseClientConfig databaseClientConfig = new DatabaseClientConfig()

	@TaskAction
	void runUnitTests() {
		try {
			Class.forName("com.marklogic.test.unit.TestManager")
		} catch (Exception ex) {
			def message = "This task requires the com.marklogic:marklogic-unit-test-client library to be a buildscript dependency"
			throw new GradleException(message)
		}

		def appConfig = getAppConfig()

		def client
		if (databaseClientConfig.getPort() != null && databaseClientConfig.getPort() > 0) {
			println "Constructing DatabaseClient based on settings in the databaseClientConfig task property"
			client = appConfig.configuredDatabaseClientFactory.newDatabaseClient(databaseClientConfig)
		} else if (appConfig.getTestRestPort() != null) {
			println "Constructing DatabaseClient that will connect to port: " + appConfig.getTestRestPort()
			client = appConfig.newTestDatabaseClient()
		} else {
			println "Constructing DatabaseClient that will connect to port: " + appConfig.getRestPort()
			client = appConfig.newDatabaseClient()
		}

		try {
			def testManager = new TestManager(client)

			boolean runTeardown = true
			boolean runSuiteTeardown = true
			boolean runCoverage = false
			if (project.hasProperty("runTeardown")) {
				runTeardown = Boolean.parseBoolean(project.property("runTeardown"))
			}
			if (project.hasProperty("runSuiteTeardown")) {
				runSuiteTeardown = Boolean.parseBoolean(project.property("runSuiteTeardown"))
			}
			if (project.hasProperty("runCodeCoverage")) {
				runCoverage = Boolean.parseBoolean(project.property("runCodeCoverage"))
			}

			println "Run teardown scripts: " + runTeardown
			println "Run suite teardown scripts: " + runSuiteTeardown
			println "Run code coverage: " + runCoverage
			println "Running all suites..."
			long start = System.currentTimeMillis()
			def suites = testManager.runAllSuites(runTeardown, runSuiteTeardown, runCoverage)
			println "Done running all suites; time: " + (System.currentTimeMillis() - start) + "ms"
			def report = new DefaultJUnitTestReporter().reportOnJUnitTestSuites(suites)
			println report

			String resultsPath = "build/test-results/marklogic-unit-test"
			String resultProperty = "unitTestResultsPath"
			if (project.hasProperty(resultProperty)) {
				resultsPath = project.property(resultProperty)
			}

			File resultsDir = new File(resultsPath)
			if (resultsDir.exists()) {
				boolean deleted = resultsDir.deleteDir()
				if (!deleted) {
					println "Unable to delete test results directory: " + resultsPath
				}
			}
			resultsDir.mkdirs()

			int fileCount = 0;
			boolean testsFailed = false
			for (JUnitTestSuite suite : suites) {
				if (suite.hasTestFailures()) {
					testsFailed = true
				}
				String xml = suite.getXml()
				String filename = "TEST-" + suite.getName() + ".xml"
				org.springframework.util.FileCopyUtils.copy(xml.getBytes(), new File(resultsDir, filename))
				fileCount++;
			}

			println "\n" + fileCount + " test result files were written to: " + resultsPath

			if (testsFailed) {
				throw new GradleException("There were failing tests. See the test results at: " + resultsPath)
			}

		} finally {
			client.release()
		}
	}
}
