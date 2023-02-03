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
package com.marklogic.mgmt.util;

import com.marklogic.client.ext.helper.LoggingObject;
import org.springframework.util.StringUtils;

/**
 * Helper class for factories that depend on a PropertySource for configuring the objects they produce.
 */
public abstract class PropertySourceFactory extends LoggingObject {

	private PropertySource propertySource;
	private boolean checkWithMarklogicPrefix = true;

	protected PropertySourceFactory() {
		this(new SystemPropertySource());
	}

	public PropertySourceFactory(PropertySource propertySource) {
		this.propertySource = propertySource;
	}

	/**
	 * If checkWithMarklogicPrefix is set to true, and a property with the given name is not found, this method will
	 * check for a property with "marklogic." + the property name. This allows for Spring Boot-style properties, where
	 * it's usually helpful to be able to prefix everything with "marklogic.".
	 *
	 * @param name
	 * @return
	 */
	protected String getProperty(String name) {
		String val = propertySource.getProperty(name);
		if (val != null) {
			return val.trim();
		}
		if (checkWithMarklogicPrefix) {
			val = propertySource.getProperty("marklogic." + name);
			return val != null ? val.trim() : val;
		}
		return null;
	}

	protected boolean propertyExists(String name) {
		return StringUtils.hasText(propertySource.getProperty(name));
	}

	public void setPropertySource(PropertySource propertySource) {
		this.propertySource = propertySource;
	}

	public boolean isCheckWithMarklogicPrefix() {
		return checkWithMarklogicPrefix;
	}

	public void setCheckWithMarklogicPrefix(boolean applyMarklogicPrefix) {
		this.checkWithMarklogicPrefix = applyMarklogicPrefix;
	}

	public PropertySource getPropertySource() {
		return propertySource;
	}
}
