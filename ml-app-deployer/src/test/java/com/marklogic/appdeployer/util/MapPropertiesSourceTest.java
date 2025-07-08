/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.util;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class MapPropertiesSourceTest  {

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
