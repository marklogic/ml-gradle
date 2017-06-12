package com.marklogic.gradle.task.roxy

import com.marklogic.gradle.task.MarkLogicTask
import org.gradle.api.tasks.TaskAction

class RoxyCopyPropertiesTask extends MarkLogicTask {

	Set<String> allRoxyProperties = new LinkedHashSet<>()
	def roxyPropertyFiles = ["default.properties", "build.properties"]
	def roxyGradleMapping = [
								"user" : "mlRestAdminUsername", "password" : "mlAdminPassword",
								"app-name" : "mlAppName", "modules-root" : "mlModulePaths",
								"app-port" : "mlRestPort", "xcc-port" : "mlAppServicesPort",
								"authentication-method": "mlRestAuthentication", "appuser-password" : "mlRestAdminPassword",
								"rest-options.dir" : "mlModulePaths", "rest-ext.dir" : "mlModulePaths",
								"rest-transforms.dir" : "mlModulePaths", "xquery.dir" : "mlModulePaths",
								"group" : "mlGroupName", "schemas.dir" : "mlSchemasPath",
								"content-db" : "mlContentDatabaseName", "modules-db" : "mlModulesDatabaseName",
								"content-forests-per-host" : "mlContentForestsPerHost", "test-port" : "mlTestRestPort"
							]

	@TaskAction
	void copyProperties() {
		if (getRoxyHome()) {
			Map roxyProperties = new LinkedHashMap()
			roxyPropertyFiles.each { propertyFile ->
				new File(getRoxyHome() + "/deploy/", propertyFile).eachLine { line ->
					if (!line.startsWith("#") && !line.isEmpty()) {
						def keyValue = line.split("=")
						if(keyValue.length == 2)
							roxyProperties.put(keyValue[0], keyValue[1])
						allRoxyProperties.add(keyValue[0])
					}
				}
			}
			writeFile("gradle.properties", roxyProperties)
		}else{
			println "mlRoxyHome parameter is not provided. Please run using -P mlRoxyHome=/your/roxy/project/home";
		}
	}

	void writeFile(String filename, Map roxyProperties) {
		File file = new File(filename);
		if (file.exists()) {
			new File("backup-" + filename).write(file.text)
		}
		file.withWriter { writer ->
			roxyGradleMapping.each { k, v ->
				def val = roxyProperties.get(k)
				if (val) writer.append(v).append("=").append(val).append("\n")
			}
			allRoxyProperties.removeAll(roxyGradleMapping.keySet())
			allRoxyProperties.each{ prop ->
				writer.append(prop).append("=").append("unmapped").append("\n")
			}
		}
	}

	String getRoxyHome(){
		project.hasProperty("mlRoxyHome") ? project.property("mlRoxyHome") : ""
	}
}
