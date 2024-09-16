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
package com.marklogic.gradle.task.roxy

import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

class RoxyMigratePropertiesTask extends RoxyTask {

	@Input
	@Optional
	def roxyPropertyFiles = ["default.properties", "build.properties"]

	@Input
	@Optional
	def roxyGradleMapping = [
		"app-name"                : "mlAppName",
		"app-port"                : "mlRestPort",
		"appuser-password"        : "mlRestAdminPassword",
		"authentication-method"   : "mlRestAuthentication",
		"content-db"              : "mlContentDatabaseName",
		"content-forests-per-host": "mlContentForestsPerHost",
		"group"                   : "mlGroupName",
		"modules-db"              : "mlModulesDatabaseName",
		"password"                : "mlPassword",
		"test-port"               : "mlTestRestPort",
		"user"                    : "mlUsername"
	]

	@TaskAction
	void copyProperties() {
		if (null != getRoxyProjectPath()) {
			Map roxyProperties = buildMapOfRoxyProperties()
			writeFile("gradle.properties", constructText(roxyProperties))
		} else {
			printMissingPathMessage()
		}
	}

	Map buildMapOfRoxyProperties() {
		Map roxyProperties = new LinkedHashMap()
		roxyPropertyFiles.each { propertyFile ->
			File file = new File(getRoxyProjectPath() + "/deploy/", propertyFile)
			if (file.exists()) {
				file.eachLine { line ->
					if (!line.startsWith("#") && !line.isEmpty()) {
						def keyValue = line.split("=")
						if (keyValue.length == 2) {
							roxyProperties.put(keyValue[0], keyValue[1])
						}
					}
				}
			} else {
				println "Did not find Roxy properties file: " + getRoxyProjectPath() + "/deploy/" + propertyFile
			}
		}
		return roxyProperties
	}

	/**
	 * First write the Roxy properties that can be mapped to ml-gradle properties. Then write every other Roxy property.
	 *
	 * @param roxyProperties
	 * @return
	 */
	String constructText(Map roxyProperties) {
		def properties = new StringBuilder()

		properties.append("# Roxy properties that were mapped to ml-gradle properties\n")
		roxyGradleMapping.each { entry ->
			def val = roxyProperties.get(entry.key)
			if (val != null) {
				properties.append(entry.value).append("=").append(val).append("\n")
			}
		}

		properties.append("\n# Added automatically by ml-gradle under the assumption that a Roxy application defines all of the app servers that it needs ")
		properties.append("\n# and thus doesn't need ml-gradle to automatically create a REST API server for it.")
		properties.append("\nmlNoRestServer=true\n")

		properties.append("\n# All other Roxy properties\n")
		roxyProperties.each { entry ->
			String key = entry.key
			if (roxyGradleMapping.containsKey(key)) {
				key = roxyGradleMapping.get(key)
				println "Mapping Roxy property '" + entry.key + "' to ml-gradle property '" + key + "'"
			}
			def val = roxyProperties.get(entry.key)
			if (null != val) {
				properties.append(key).append("=").append(val).append("\n")
			}
		}
		return properties
	}

	void writeFile(String filename, String text) {
		File file = new File(filename);
		if (file.exists()) {
			new File("backup-" + filename).write(file.text)
		}
		println "Writing: " + filename
		file.write(text)
	}

}
