package com.marklogic.appdeployer.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class MapPropertiesSourceTest extends Assert {

	@Test
	public void propertiesWithNull() {
		Map<String, String> map = new HashMap<>();
		map.put("hello", "world");
		map.put("isNull", null);

		Properties props = new MapPropertiesSource(map).getProperties();
		assertEquals("world", props.getProperty("hello"));
		assertFalse(props.containsKey("isNull"));
	}
}
