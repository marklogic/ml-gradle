/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.ext.datamovement.job;

import java.util.function.Consumer;

public class SimpleJobProperty implements JobProperty {

	private String name;
	private String description;
	private Consumer<String> propertyValueConsumer;
	private boolean required;

	public SimpleJobProperty(String name, String description, Consumer<String> propertyValueConsumer) {
		this.name = name;
		this.description = description;
		this.propertyValueConsumer = propertyValueConsumer;
	}

	@Override
	public String getPropertyName() {
		return name;
	}

	@Override
	public String getPropertyDescription() {
		return description;
	}

	@Override
	public Consumer<String> getPropertyValueConsumer() {
		return propertyValueConsumer;
	}

	@Override
	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}
}
