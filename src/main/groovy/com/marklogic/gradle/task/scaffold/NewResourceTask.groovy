/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
