/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.ext.tokenreplacer;

import java.util.Properties;

/**
 * Intent is to allow for a Properties object to be provided to DefaultTokenReplacer without that class knowing
 * where the properties are retrieved from.
 */
public interface PropertiesSource {

	Properties getProperties();
}
