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

import org.apache.commons.io.FileUtils
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

class RoxyMigrateFilesTask extends RoxyTask {

	@Input
	@Optional
	def roxyFolderMapping = [
								"src" : "/root",
								"rest-api/config/options" : "/options",
								"rest-api/ext" : "/services",
								"rest-api/transforms" : "/transforms"
							]

	@TaskAction
	void copyRoxyFiles() {
		if (getRoxyProjectPath()) {
			def baseDir = getAppConfig().getModulePaths().get(0)

			roxyFolderMapping.each { k, v ->
				def sourcePath = getRoxyProjectPath() + "/" + k
				def sourceFolder = new File(sourcePath)
				if (sourceFolder.exists() && sourceFolder.isDirectory()) {
					def targetDir = baseDir + v
					println "Creating directory: " + targetDir
					def targetFolder = new File(targetDir)
					FileUtils.forceMkdir(targetFolder)
					println "Copying contents of '" + sourcePath + "' to '" + targetDir + "'"
					FileUtils.copyDirectory(sourceFolder, targetFolder)
				} else {
					println "Did not find Roxy source directory: " + sourcePath
				}
			}

			// Check for REST properties file
			File configDir = new File(getRoxyProjectPath() + "/rest-api/config");
			if (configDir.exists() && configDir.isDirectory()) {
				File propertiesFile = new File(configDir, "properties.xml");
				if (propertiesFile.exists() && propertiesFile.isFile()) {
					File targetFile = new File(baseDir, "rest-properties.xml")
					println "Copying rest-api/config/properties.xml to " + targetFile
					FileUtils.copyFile(propertiesFile, targetFile)
				}
			}
		} else {
			printMissingPathMessage()
		}
	}

}
