package com.marklogic.gradle.task.tests

import com.marklogic.appdeployer.AppConfig
import com.marklogic.gradle.task.MarkLogicTask
import com.marklogic.mgmt.ManageClient
import com.marklogic.mgmt.ManageConfig
import com.marklogic.rest.util.Fragment
import org.gradle.api.tasks.TaskAction


class RoxyTest extends MarkLogicTask{

	@TaskAction
	void runAllTests() {
		AppConfig config = getAppConfig()
		String testResultDir = getProject().getProperties().get("mlTestResultDir")
		String roxyResultDir = "${testResultDir}/roxy-tests"
		ManageClient testClient = new ManageClient(
			new ManageConfig(config.getHost(), config.getTestRestPort(), getAdminUsername(), getAdminPassword()))
		String xml = testClient.getXml("/test/default.xqy?func=list","rt",
			"http://marklogic.com/roxy/test").getPrettyXml()
		if (logger.isDebugEnabled()) {
			logger.debug("Found Roxy tests:")
			logger.debug(xml)
		}
		def tests = new XmlSlurper().parseText(xml)

		tests.suite.each {
			String suiteName = it.@path
			String suiteTests = it.tests.test.@path.findAll{
					!["suite-setup.xqy", "suite-teardown.xqy", "test-setup.xqy","test-teardown.xqy"].contains(it)
				}.join(',')
			String suiteUrl = "/test/default.xqy?&func=run&format=junit&runsuitetestsetup=true&runsuiteteardown=true" +
				"&suite=${urlEncode(suiteName)}&tests=${urlEncode(suiteTests)}"

			logger.info("Running tests for suite: " + suiteName)
			Fragment resultXml = testClient.getXml(suiteUrl)
			File resultsDir = new File(roxyResultDir)
			resultsDir.isDirectory() ? null : resultsDir.mkdirs()

			String filename = "${roxyResultDir}/${suiteName}.xml"
			File junitXmlFile = new File(filename)
			logger.info("Writing result XML to " + filename)
			junitXmlFile.write resultXml.getPrettyXml()
		}

		println "Finished writing test results to ${roxyResultDir}"
	}

	static String urlEncode(String string) {
		return URLEncoder.encode(string, "UTF-8")
	}
}
