package com.marklogic.gradle.task.datamovement

import com.marklogic.client.DatabaseClient
import com.marklogic.client.datamovement.ExportListener
import com.marklogic.client.ext.datamovement.QueryBatcherTemplate
import com.marklogic.client.ext.datamovement.consumer.WriteToFileConsumer
import org.gradle.api.tasks.TaskAction

class ExportModulesTask extends DataMovementTask {

	@TaskAction
	void exportModules() {
		String exportPath
		String uriPattern = "**"
		String databaseName = getAppConfig().getModulesDatabaseName()
		boolean logErrors = true

		if (project.hasProperty("exportPath")) {
			exportPath = project.property("exportPath")
		} else {
			List<String> modulePaths = getAppConfig().getModulePaths()
			if (modulePaths == null || modulePaths.isEmpty()) {
				println "Cannot export modules, no module paths are defined; can use -PexportPath= to define a path to export to"
				return
			}

			// Use the last path; if there are multiple, the ones at the beginning may be from dependencies
			exportPath = modulePaths.get(modulePaths.size() - 1) + "/root"
		}

		if (project.hasProperty("uriPattern")) {
			uriPattern = project.property("uriPattern")
		}

		if (project.hasProperty("databaseName")) {
			databaseName = project.property("databaseName")
		}

		if (project.hasProperty("logErrors")) {
			logErrors = Boolean.parseBoolean(project.property("logErrors"))
		}

		File out = new File(exportPath)
		println "Will export modules from database '" + databaseName + "' matching pattern '" + uriPattern + "' to path: " + out.getAbsolutePath()

		DatabaseClient client = getAppConfig().newAppServicesDatabaseClient(databaseName)
		try {
			WriteToFileConsumer consumer = new WriteToFileConsumer(out)
			consumer.setLogErrors(logErrors)

			QueryBatcherTemplate template = newQueryBatcherTemplate(client)
			ExportListener listener = new ExportListener()
			listener.onDocumentReady(consumer)
			template.applyOnUriPattern(listener, "**")
		} finally {
			client.release()
		}
	}
}
