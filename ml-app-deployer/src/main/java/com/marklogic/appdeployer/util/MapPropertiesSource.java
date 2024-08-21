/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
