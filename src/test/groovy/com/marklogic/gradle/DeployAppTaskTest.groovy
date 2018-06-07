package com.marklogic.gradle

import org.gradle.testkit.runner.UnexpectedBuildFailure

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class DeployAppTaskTest extends BaseTest {

	def "deploy barebones app"() {
		setup:
		print(runTask('mlUnDeploy',  '-Pconfirm=true').output)

		expect:
		def srf = getServerConfig()
		def drf = getDbConfig()
		!srf.resourceExists("my-app")
		!drf.resourceExists("my-app-content")
		!drf.resourceExists("my-app-modules")

		when:
		def result = runTask('mlDeploy')
		print(result.output)

		then:
		notThrown(UnexpectedBuildFailure)
		result.task(":mlDeploy").outcome == SUCCESS
		def srf2 = getServerConfig()
		def drf2 = getDbConfig()
		srf2.resourceExists("my-app")
		drf2.resourceExists("my-app-content")
		drf2.resourceExists("my-app-modules")

		cleanup:
		runTask('mlUndeploy',  '-Pconfirm=true')
	}

}
