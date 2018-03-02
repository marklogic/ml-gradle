package com.marklogic.gradle.task.scaffold

import com.marklogic.appdeployer.scaffold.DefaultResourceWriter
import com.marklogic.gradle.task.MarkLogicTask
import com.marklogic.mgmt.api.Resource
import com.marklogic.mgmt.template.TemplateBuilder

class NewResourceTask extends MarkLogicTask {

	/**
	 * Only properties starting with "ml-" are passed to the TemplateBuilder. This is so that properties like e.g.
	 * "description" can be set, as "description" is already defined by Gradle and can't be overridden.
	 *
	 * @param templateBuilder
	 */
	void createResourceFile(TemplateBuilder templateBuilder) {
		Map<String, Object> propertyMap = new HashMap<>()
		Map props = getProject().getProperties()
		for (String key : props.keySet()) {
			if (key.startsWith("ml-")) {
				propertyMap.put(key.substring(3), props.get(key))
			}
		}

		println "Setting resource properties using map: " + propertyMap
		Resource r = templateBuilder.buildTemplate(propertyMap)
		File f = new DefaultResourceWriter().writeResourceAsJson(r, getAppConfig().getFirstConfigDir())
		println "Created file: " + f
	}
}
