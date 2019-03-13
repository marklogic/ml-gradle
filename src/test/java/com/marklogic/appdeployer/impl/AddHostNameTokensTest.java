package com.marklogic.appdeployer.impl;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import org.junit.Test;

public class AddHostNameTokensTest extends AbstractAppDeployerTest {

	@Test
	public void withPropertySet() {
		assertFalse(appConfig.getCustomTokens().containsKey("mlHostName1"));

		appConfig.setAddHostNameTokens(true);
		initializeAppDeployer();
		deploySampleApp();

		assertTrue(appConfig.getCustomTokens().containsKey("mlHostName1"));
	}

	@Test
	public void withPropertyNotSet() {
		initializeAppDeployer();
		deploySampleApp();

		assertFalse(appConfig.getCustomTokens().containsKey("mlHostName1"));
	}
}
