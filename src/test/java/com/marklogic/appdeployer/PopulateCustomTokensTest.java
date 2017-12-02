package com.marklogic.appdeployer;

import com.marklogic.appdeployer.util.SimplePropertiesSource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.Properties;

public class PopulateCustomTokensTest extends Assert {

	private AppConfig appConfig;
	private Properties props;

	@Before
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
