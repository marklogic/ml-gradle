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
package com.marklogic.client.ext.tokenreplacer;

import com.marklogic.client.ext.helper.LoggingObject;
import org.springframework.beans.factory.config.PlaceholderConfigurerSupport;
import org.springframework.util.PropertyPlaceholderHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Default implementation of TokenReplacer that relies on a list of PropertiesSource implementations for
 * finding tokens to replace in text.
 *
 * The propertyPrefix property allows for adhering to the Roxy convention of referencing properties in module text
 * with "@ml." as a prefix. This class does not look for that prefix; rather, the RoxyTokenReplacer subclass does. This
 * class allows for that prefix to be set to anything.
 */
public class DefaultTokenReplacer extends LoggingObject implements TokenReplacer {

	private Properties properties;
	private PropertyPlaceholderHelper helper;
	private List<PropertiesSource> propertiesSources = new ArrayList<>();
	private String propertyPrefix;

	public void addPropertiesSource(PropertiesSource source) {
		this.propertiesSources.add(source);
	}

	protected void initializeHelper() {
		helper = new PropertyPlaceholderHelper(PlaceholderConfigurerSupport.DEFAULT_PLACEHOLDER_PREFIX,
			PlaceholderConfigurerSupport.DEFAULT_PLACEHOLDER_SUFFIX,
			PlaceholderConfigurerSupport.DEFAULT_VALUE_SEPARATOR, true);
	}

	/**
	 * Initialize the Properties instance based on all the PropertiesSources that have been registered.
	 */
	protected void initializeProperties() {
		properties = new Properties();
		for (PropertiesSource source : propertiesSources) {
			Properties p = source.getProperties();
			if (p != null) {
				properties.putAll(p);
			}
		}
	}

	@Override
	public String replaceTokens(String text) {
		if (properties == null) {
			initializeProperties();
		}
		if (helper == null) {
			initializeHelper();
		}

		for (Object key : properties.keySet()) {
			String skey = propertyPrefix != null ? propertyPrefix + key : key.toString();
			if (logger.isTraceEnabled()) {
				logger.trace("Checking for key in text: " + skey);
			}
			if (text.contains(skey)) {
				String value = properties.getProperty(key.toString());
				value = helper.replacePlaceholders(value, properties);
				if (logger.isDebugEnabled()) {
					logger.debug(format("Replacing %s with %s", skey, value));
				}
				text = text.replace(skey, value);
			}
		}
		return text;
	}

	public List<PropertiesSource> getPropertiesSources() {
		return propertiesSources;
	}

	public void setPropertiesSources(List<PropertiesSource> propertiesSources) {
		this.propertiesSources = propertiesSources;
	}

	public void setPropertyPlaceholderHelper(PropertyPlaceholderHelper helper) {
		this.helper = helper;
	}

	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public void setPropertyPrefix(String propertyPrefix) {
		this.propertyPrefix = propertyPrefix;
	}
}
