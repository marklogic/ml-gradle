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
package com.marklogic.appdeployer;

import com.marklogic.appdeployer.util.SimplePropertiesSource;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Properties;

public class PopulateCustomTokensTest  {

	private AppConfig appConfig;
	private Properties props;

	@BeforeEach
	public void setup() {
		appConfig = new AppConfig();

		props = new Properties();
		props.setProperty("color", "blue");
		props.setProperty("size", "M");
	}

	@Test
	public void defaultPrefixAndSuffix() {
		appConfig.populateCustomTokens(new SimplePropertiesSource(props));

		Map<String, String> tokens = appConfig.getCustomTokens();
		assertEquals("blue", tokens.get("%%color%%"));
		assertEquals("M", tokens.get("%%size%%"));
	}

	@Test
	public void customPrefixAndSuffix() {
		appConfig.populateCustomTokens(new SimplePropertiesSource(props), "!!", "!!");

		Map<String, String> tokens = appConfig.getCustomTokens();
		assertEquals("blue", tokens.get("!!color!!"));
		assertEquals("M", tokens.get("!!size!!"));
	}

	@Test
	public void onlyPrefix() {
		appConfig.populateCustomTokens(new SimplePropertiesSource(props), "!!", null);

		Map<String, String> tokens = appConfig.getCustomTokens();
		assertEquals("blue", tokens.get("!!color"));
		assertEquals("M", tokens.get("!!size"));
	}

	@Test
	public void onlySuffix() {
		appConfig.populateCustomTokens(new SimplePropertiesSource(props), null, "!!");

		Map<String, String> tokens = appConfig.getCustomTokens();
		assertEquals("blue", tokens.get("color!!"));
		assertEquals("M", tokens.get("size!!"));
	}
}
