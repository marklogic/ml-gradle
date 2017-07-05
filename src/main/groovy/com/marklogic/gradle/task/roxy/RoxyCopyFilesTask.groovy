package com.marklogic.gradle.task.roxy

import com.marklogic.gradle.task.MarkLogicTask
import org.apache.commons.io.FileUtils
import org.gradle.api.tasks.TaskAction

class RoxyCopyFilesTask extends MarkLogicTask {

	def roxyFolderMapping = [
								"src" : "/root",
								"rest-api/config" : "/options",
								"rest-api/ext" : "/services",
								"rest-api/transforms" : "/transforms"
							]

	@TaskAction
	void copyRoxyFiles() {
		if (getRoxyHome()) {
			def baseDir = getAppConfig().getModulePaths().get(0)
			roxyFolderMapping.each { k, v ->
				def source = getRoxyHome() + "/" + k
				println "Source folder '" + source + "' ... "
				def sourceFolder = new File(source)
				if (sourceFolder.exists() && sourceFolder.isDirectory()) {
					def targetDir = baseDir + v
					println "Creating folder '" + targetDir + "' ... "
					def targetFolder = new File(targetDir)
					FileUtils.forceMkdir(targetFolder)
					println "Copying contents of '" + source + "' to '" + targetDir + "' ... "
					FileUtils.copyDirectory(sourceFolder, targetFolder)
				}
			}
		} else {
			println "mlRoxyHome parameter is not provided. Please run using -PmlRoxyHome=/your/roxy/project/home"
		}
	}

	String getRoxyHome(){
		project.hasProperty("mlRoxyHome") ? project.property("mlRoxyHome") : ""
	}
}
