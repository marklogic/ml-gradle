/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer;

import com.marklogic.appdeployer.util.SimplePropertiesSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PopulateCustomTokensTest {

	private AppConfig appConfig;
	private Properties props;

	@BeforeEach
	void setup() {
		appConfig = new AppConfig();

		props = new Properties();
		props.setProperty("color", "blue");
		props.setProperty("size", "M");
	}

	@Test
	void defaultPrefixAndSuffix() {
		appConfig.populateCustomTokens(new SimplePropertiesSource(props));

		Map<String, String> tokens = appConfig.getCustomTokens();
		assertEquals("blue", tokens.get("%%color%%"));
		assertEquals("M", tokens.get("%%size%%"));
	}

	@Test
	void customPrefixAndSuffix() {
		appConfig.populateCustomTokens(new SimplePropertiesSource(props), "!!", "!!");

		Map<String, String> tokens = appConfig.getCustomTokens();
		assertEquals("blue", tokens.get("!!color!!"));
		assertEquals("M", tokens.get("!!size!!"));
	}

	@Test
	void onlyPrefix() {
		appConfig.populateCustomTokens(new SimplePropertiesSource(props), "!!", null);

		Map<String, String> tokens = appConfig.getCustomTokens();
		assertEquals("blue", tokens.get("!!color"));
		assertEquals("M", tokens.get("!!size"));
	}

	@Test
	void onlySuffix() {
		appConfig.populateCustomTokens(new SimplePropertiesSource(props), null, "!!");

		Map<String, String> tokens = appConfig.getCustomTokens();
		assertEquals("blue", tokens.get("color!!"));
		assertEquals("M", tokens.get("size!!"));
	}

	@Test
	void whitespace() {
		props.setProperty("someProp", "has space ");

		appConfig.populateCustomTokens(new SimplePropertiesSource(props));
		Map<String, String> tokens = appConfig.getCustomTokens();
		assertEquals("has space", tokens.get("%%someProp%%"));
	}
}
