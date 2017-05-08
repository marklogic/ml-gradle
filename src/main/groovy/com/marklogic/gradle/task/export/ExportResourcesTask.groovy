package com.marklogic.gradle.task.export

import com.marklogic.appdeployer.export.ExportedResources
import com.marklogic.appdeployer.export.Exporter
import com.marklogic.gradle.task.MarkLogicTask
import com.marklogic.mgmt.selector.PrefixResourceSelector
import com.marklogic.mgmt.selector.PropertiesFileResourceSelector
import com.marklogic.mgmt.selector.RegexResourceSelector
import com.marklogic.mgmt.selector.ResourceSelector
import org.gradle.api.tasks.TaskAction

class ExportResourcesTask extends MarkLogicTask {

	@TaskAction
	void exportResources() {
		String filePropName = "propertiesFile"
		String prefixPropName = "prefix"
		String regexPropName = "regex"

		String includeTypesPropName = "includeTypes"
		String includeTypes = null
		if (getProject().hasProperty(includeTypesPropName)) {
			includeTypes = getProject().property(includeTypesPropName).split(",")
		}

		if (getProject().hasProperty(filePropName)) {
			String filename = getProject().property(filePropName)
			File file = new File(filename)
			if (file.exists()) {
				export(new PropertiesFileResourceSelector(file))
			} else {
				println "File " + filename + " does not exist"
			}
		} else if (getProject().hasProperty(prefixPropName)) {
			String prefix = getProject().property(prefixPropName)
			PrefixResourceSelector selector = new PrefixResourceSelector(prefix)
			if (includeTypes != null) {
				selector.setIncludeTypes(includeTypes)
			}
			export(selector)
		} else if (getProject().hasProperty(regexPropName)) {
			String regex = getProject().property(regexPropName)
			RegexResourceSelector selector = new RegexResourceSelector(regex)
			if (includeTypes != null) {
				selector.setIncludeTypes(includeTypes)
			}
			export(selector)
		} else {
			println "Use -PpropertiesFile or -Pprefix or -Pregex to specify the resources to export, with -PincludeTypes for restricting the types to export when using -Pprefix or -Pregex"
		}
	}

	void export(ResourceSelector selector) {
		ExportedResources resources = new Exporter(getManageClient()).
			select(selector).
			export(getAppConfig().getConfigDir().getBaseDir())
		println "Exported files:"
		for (File f : resources.getFiles()) {
			println f.getAbsolutePath()
		}

		println "Export messages:"
		for (String s : resources.getMessages()) {
			println s
		}
	}
}
