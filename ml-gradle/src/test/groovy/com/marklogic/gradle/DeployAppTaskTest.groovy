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
