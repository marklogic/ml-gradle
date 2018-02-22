package com.marklogic.gradle.task.export

import com.marklogic.appdeployer.export.ExportedResources
import com.marklogic.appdeployer.export.Exporter
import com.marklogic.gradle.task.MarkLogicTask
import com.marklogic.mgmt.selector.PrefixResourceSelector
import com.marklogic.mgmt.selector.PropertiesResourceSelector
import com.marklogic.mgmt.selector.RegexResourceSelector
import com.marklogic.mgmt.selector.ResourceSelector
import org.gradle.api.tasks.TaskAction

class ExportResourcesTask extends MarkLogicTask {

	@TaskAction
	void exportResources() {
		String filePropName = "propertiesFile"
		String prefixPropName = "prefix"
		String regexPropName = "regex"
		String triggersDatabase = getAppConfig().getTriggersDatabaseName()

		String includeTypesPropName = "includeTypes"
		String includeTypes = null
		if (getProject().hasProperty(includeTypesPropName)) {
			includeTypes = getProject().property(includeTypesPropName)
		}

		if (getProject().hasProperty(filePropName)) {
			String filename = getProject().property(filePropName)
			File file = new File(filename)
			if (file.exists()) {
				export(new PropertiesResourceSelector(file))
			} else {
				println "File " + filename + " does not exist"
			}
		} else if (getProject().hasProperty(prefixPropName)) {
			String prefix = getProject().property(prefixPropName)
			PrefixResourceSelector selector = new PrefixResourceSelector(prefix)
			selector.setTriggersDatabase(triggersDatabase)
			selector.setIncludeTypesAsString(includeTypes)
			export(selector)
		} else if (getProject().hasProperty(regexPropName)) {
			String regex = getProject().property(regexPropName)
			RegexResourceSelector selector = new RegexResourceSelector(regex)
			selector.setTriggersDatabase(triggersDatabase)
			selector.setIncludeTypesAsString(includeTypes)
			export(selector)
		} else {
			println "Use -PpropertiesFile or -Pprefix or -Pregex to specify the resources to export, with -PincludeTypes=comma-delimited-string for restricting the types to export when using -Pprefix or -Pregex, and -PexportPath to customize where to export the resources to"
		}
	}

	void export(ResourceSelector selector) {
		def path = getAppConfig().getConfigDir().getBaseDir()
		if (getProject().hasProperty("exportPath")) {
			path = new File(getProject().property("exportPath"))
		}
		println "Exporting resources to: " + path

		Exporter exporter
		if (project.hasProperty("mlGroupName")) {
			String group = project.property("mlGroupName")
			println "Will export servers and tasks in group: " + group
			exporter = new Exporter(getManageClient(), group)
		} else {
			exporter = new Exporter(getManageClient())
		}

		ExportedResources resources = exporter
			.select(selector)
			.withTriggersDatabase(getAppConfig().getTriggersDatabaseName())
			.export(path)

		println "Exported files:"
		if (resources.getFiles() != null) {
			for (File f : resources.getFiles()) {
				println f.getAbsolutePath()
			}
		}

		println "Export messages:"
		if (resources.getMessages() != null) {
			for (String s : resources.getMessages()) {
				println s
			}
		}
	}
}
