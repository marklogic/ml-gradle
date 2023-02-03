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
package com.marklogic.gradle;

import com.marklogic.client.ext.helper.LoggingObject;
import com.marklogic.client.ext.tokenreplacer.PropertiesSource;
import com.marklogic.mgmt.util.PropertySource;
import org.gradle.api.Project;

import java.util.Map;
import java.util.Properties;

/**
 * TODO Would be nice to combine PropertySource and PropertiesSource, if possible.
 */
public class ProjectPropertySource extends LoggingObject implements PropertySource, PropertiesSource {

	private Project project;

	public ProjectPropertySource(Project project) {
		this.project = project;
	}

	@Override
	public String getProperty(String name) {
		return project.hasProperty(name) ? project.property(name).toString() : null;
	}

	/**
	 * Build the Properties instance each time this is called, ensuring we get the latest set of properties
	 * from the Gradle Project.
	 *
	 * This currently only includes properties that start with "ml". This is to avoid picking up very generically-named
	 * properties in Gradle, such as "name" and "version", which may conflict with properties from other sources.
	 */
	@Override
	public Properties getProperties() {
		Properties props = new Properties();
		Map<String, ?> map = project.getProperties();
		for (String key : map.keySet()) {
			if (key != null && key.startsWith("ml")) {
				Object val = map.get(key);
				if (val != null) {
					if (logger.isDebugEnabled()) {
						logger.debug("Including key " + key + " for replacing module tokens");
					}
					props.setProperty(key, val.toString());
				}
			}
		}
		return props;
	}
}
