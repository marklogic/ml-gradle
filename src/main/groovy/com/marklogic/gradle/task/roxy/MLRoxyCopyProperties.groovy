package com.marklogic.gradle.task.roxy

import com.marklogic.gradle.task.MarkLogicTask
import org.gradle.api.tasks.TaskAction

class MLRoxyCopyProperties extends MarkLogicTask {

	def roxyPropertyFiles = ["default.properties", "build.properties"]
	def roxyGradleMapping = ["app-name" : "mlAppName", "app-port" : "mlRestPort",
							 "user" : "mlRestAdminUsername", "password" : "mlAdminPassword",
							 "content-forests-per-host" : "mlContentForestsPerHost", "group" : "mlGroupName",
							 "forest-data-dir": "envForestDataDirectory"]

	@TaskAction
	void copyProperties() {
		if (null != getRoxyHome()) {
			Map roxyProperties = new LinkedHashMap()
			roxyPropertyFiles.each { propertyFile ->
				new File(getRoxyHome() + "/deploy/", propertyFile).eachLine { line ->
					if (!line.startsWith("#")) {
						def keyValue = line.split("=")
						if(keyValue.length == 2)
						roxyProperties.put(keyValue[0], keyValue[1])
					}
				}
			}
			writeFile("gradle.properties", constructText(roxyProperties))
		}else{
			println "mlRoxyHome parameter is not provided. Please run using -P mlRoxyHome=/your/project/home";
		}
	}

	String constructText(Map roxyProperties){
		def properties = new StringBuilder()
		roxyGradleMapping.each { entry ->
			def val = roxyProperties.get(entry.key)
			if(null != val)
			properties.append(entry.value).append("=").append(val).append("\n")
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
