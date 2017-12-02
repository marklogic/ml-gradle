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
