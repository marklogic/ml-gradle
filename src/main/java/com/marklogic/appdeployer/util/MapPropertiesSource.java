package com.marklogic.appdeployer.util;

import com.marklogic.client.ext.tokenreplacer.PropertiesSource;

import java.util.Map;
import java.util.Properties;

public class MapPropertiesSource implements PropertiesSource {

	private Map<String, String> map;

	public MapPropertiesSource(Map<String, String> map) {
		this.map = map;
	}

	/**
	 * Lazily builds a Properties object based on the Map contained in this class.
	 *
	 * @return
	 */
	@Override
	public Properties getProperties() {
		Properties props = new Properties();
		for (String key : map.keySet()) {
			String value = map.get(key);
			if (value != null) {
				props.setProperty(key, value);
			}
		}
		return props;
	}
}
