/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.ext.datamovement.job;

import java.util.function.Consumer;

public interface JobProperty {

	String getPropertyName();

	String getPropertyDescription();

	Consumer<String> getPropertyValueConsumer();

	boolean isRequired();
}
