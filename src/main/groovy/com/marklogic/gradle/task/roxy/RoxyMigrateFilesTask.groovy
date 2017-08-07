package com.marklogic.gradle.task.roxy

import org.apache.commons.io.FileUtils
import org.gradle.api.tasks.TaskAction

class RoxyMigrateFilesTask extends RoxyTask {

	def roxyFolderMapping = [
								"src" : "/root",
								"rest-api/config" : "/options",
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
		} else {
			printMissingPathMessage()
		}
	}

}
