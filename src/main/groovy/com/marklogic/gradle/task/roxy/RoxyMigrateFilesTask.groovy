package com.marklogic.gradle.task.roxy

import org.apache.commons.io.FileUtils
import org.gradle.api.tasks.TaskAction

class RoxyMigrateFilesTask extends RoxyTask {

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
