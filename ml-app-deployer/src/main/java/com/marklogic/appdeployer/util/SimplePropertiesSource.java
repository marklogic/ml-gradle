/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.util;

import com.marklogic.client.ext.tokenreplacer.PropertiesSource;

import java.util.Properties;

public class SimplePropertiesSource implements PropertiesSource {

	private Properties props;

	public SimplePropertiesSource(Properties props) {
		this.props = props;
	}

	@Override
	public Properties getProperties() {
		return props;
	}
}
