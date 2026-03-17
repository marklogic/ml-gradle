/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.command.pdc;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import org.junit.jupiter.api.Test;

import java.io.File;

/**
 * We don't yet have a PDC instance for automated tests, so this test does what it can in the absence of that.
 * For now, relying on manual testing via the DeployPdcEndpointsDebug class.
 */
class DeployMarkLogicEndpointsTest extends AbstractAppDeployerTest {

	@Test
	void dontDeployWhenCloudApiKeyIsNotSet() {
		initializeAppConfig(new File("src/test/resources/cloud-project"));
		initializeAppDeployer(new DeployMarkLogicEndpointsCommand());

		deploySampleApp();
	}
}
