/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
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

	protected final Integer propertyToInteger(String propertyName, String propertyValue) {
		try {
			return Integer.parseInt(propertyValue);
		} catch (NumberFormatException ex) {
			throw new IllegalArgumentException(format("The property %s requires a numeric value; invalid value: ‘%s'", propertyName, propertyValue));
		}
	}

}
