/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.command.cma;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.mgmt.resource.forests.ForestManager;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DeployConfigurationsTest extends AbstractAppDeployerTest {

	@Test
	public void deployJsonConfig() {
		setConfigBaseDir("sample-app/cma");
		appConfig.setResourceFilenamesIncludePattern(Pattern.compile(".*json"));
		initializeAppDeployer(new DeployConfigurationsCommand());
		deploySampleApp();

		verifyForestsWereCreated();
	}

	@Test
	public void deployXmlConfig() {
		setConfigBaseDir("sample-app/cma");
		appConfig.setResourceFilenamesIncludePattern(Pattern.compile(".*xml"));
		initializeAppDeployer(new DeployConfigurationsCommand());
		deploySampleApp();

		verifyForestsWereCreated();
	}

	private void verifyForestsWereCreated() {
		ForestManager forestManager = new ForestManager(manageClient);
		final String[] configForestNames = new String[]{"cma-test-f1", "cma-test-f2", "cma-test-f3"};
		try {
			for (String name : configForestNames) {
				assertTrue(forestManager.exists(name));
			}
		} finally {
			for (String name : configForestNames) {
				if (forestManager.exists(name)) {
					forestManager.delete(name, ForestManager.DELETE_LEVEL_FULL);
				}
			}
		}
	}
}
