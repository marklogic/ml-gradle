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
package com.marklogic.mgmt.template;

import com.marklogic.client.ext.helper.LoggingObject;
import com.marklogic.mgmt.api.Resource;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Map;

public class GenericTemplateBuilder extends LoggingObject implements TemplateBuilder {

	private Map<String, Object> defaultPropertyMap;
	private Class<?> resourceClass;

	public GenericTemplateBuilder(Class<? extends Resource> resourceClass) {
		this.resourceClass = resourceClass;
		this.defaultPropertyMap = new HashMap<>();
	}

	public void addDefaultPropertyValue(String propertyName, Object value) {
		defaultPropertyMap.put(propertyName, value);
	}

	@Override
	public Resource buildTemplate(Map<String, Object> propertyMap) {
		Resource r;
		try {
			r = (Resource) this.resourceClass.newInstance();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

		final Map<String, Object> combinedMap = combinePropertyMaps(propertyMap);

		if (logger.isDebugEnabled()) {
			logger.debug("Using combined property map: " + combinedMap);
		}

		populateProperties(r, combinedMap);

		return r;
	}

	/**
	 * Uses the defaultPropertyMap and the given propertyMap to set properties on the Resource via reflection.
	 * <p>
	 * The property names are expected to match those in the Manage API - so e.g. "role-name" and not "roleName". But the
	 * Resource property via reflection is "roleName". So the Java property name is converted into a Manage API
	 * property name first - e.g. lowerCamelCase to lowercase-hyphenated.
	 *
	 * @param r
	 * @param propertyMap
	 */
	protected void populateProperties(Resource r, Map<String, Object> propertyMap) {
		BeanWrapper bw = new BeanWrapperImpl(r);
		for (PropertyDescriptor pd : bw.getPropertyDescriptors()) {
			if (pd.getWriteMethod() != null && pd.getReadMethod() != null) {
				final String javaPropertyName = pd.getName();
				final String manageApiPropertyName = toLowercaseHyphenated(javaPropertyName);
				if (propertyMap.containsKey(manageApiPropertyName)) {
					bw.setPropertyValue(javaPropertyName, propertyMap.get(manageApiPropertyName));
				}
			}

		}
	}

	protected Map<String, Object> combinePropertyMaps(Map<String, Object> propertyMap) {
		Map<String, Object> newMap = new HashMap<>();

		if (defaultPropertyMap != null) {
			for (String key : defaultPropertyMap.keySet()) {
				newMap.put(key, defaultPropertyMap.get(key));
			}
		}

		if (propertyMap != null) {
			for (String key : propertyMap.keySet()) {
				newMap.put(key, propertyMap.get(key));
			}
		}

		return newMap;
	}

	protected String toLowercaseHyphenated(String javaPropertyName) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < javaPropertyName.length(); i++) {
			char ch = javaPropertyName.charAt(i);
			if (Character.isUpperCase(ch)) {
				sb.append("-").append(Character.toLowerCase(ch));
			} else {
				sb.append(ch);
			}
		}
		return sb.toString();
	}
}
