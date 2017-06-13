package com.marklogic.client.ext.tokenreplacer;

import java.util.Properties;

/**
 * Intent is to allow for a Properties object to be provided to DefaultTokenReplacer without that class knowing
 * where the properties are retrieved from.
 */
public interface PropertiesSource {

	Properties getProperties();
}
