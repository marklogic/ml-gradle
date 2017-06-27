package com.marklogic.gradle.task.roxy

import com.marklogic.gradle.task.MarkLogicTask
import org.apache.commons.io.FileUtils
import org.gradle.api.tasks.TaskAction

import static groovy.io.FileType.FILES

class CopyRoxyFilesTask extends MarkLogicTask {

	Set<String> allRoxyProperties = new LinkedHashSet<>()

	def roxyFolderMapping = [
								"src" : "src/main/ml-modules/root",
								"rest-api/config" : "src/main/ml-modules/options",
								"rest-api/ext" : "src/main/ml-modules/services",
								"rest-api/transforms" : "src/main/ml-modules/transforms"
							]

	@TaskAction
	void copyRoxyFiles() {
		backupProperties()
		if (getRoxyHome()) {
			copyFolders()
		} else {
			println "mlRoxyHome parameter is not provided. Please run using -P mlRoxyHome=/your/roxy/project/home"
		}
	}

	void copyFolders() {
		roxyFolderMapping.each { k, v ->
			def source = getRoxyHome() + "/" + k
			def sourceFolder = new File(source)
			if (sourceFolder.exists() && sourceFolder.isDirectory()) {
				println "Creating folder '" + v + "' ... "
				def targetFolder = new File(v)
				FileUtils.forceMkdir(targetFolder)
				println "Copying contents of '" + source + "' to '" + v + "' ... "
				FileUtils.copyDirectory(sourceFolder, targetFolder)
			}
		}
	}

	void backupProperties() {
		def gradleProp = new File("gradle.properties")
		if (gradleProp.exists()) {
			println "Copying 'gradle.properties' to 'gradle.properties.backup' ... "
			FileUtils.copyFile(gradleProp, new File("gradle.properties.backup"))
		}
	}

	String getRoxyHome(){
		project.hasProperty("mlRoxyHome") ? project.property("mlRoxyHome") : ""
	}
}
