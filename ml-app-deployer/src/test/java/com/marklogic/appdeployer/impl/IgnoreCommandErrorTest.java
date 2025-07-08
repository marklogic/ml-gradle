/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.impl;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.command.databases.DeployOtherDatabasesCommand;
import com.marklogic.appdeployer.command.restapis.DeployRestApiServersCommand;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.regex.Pattern;

import static org.springframework.test.util.AssertionErrors.fail;

public class IgnoreCommandErrorTest extends AbstractAppDeployerTest {

	@Test
	public void ignoreErrorOnUndeploy() {
		appConfig.getFirstConfigDir().setBaseDir(new File("src/test/resources/sample-app/db-only-config"));

		initializeAppDeployer(new DeployRestApiServersCommand(true), new DeployOtherDatabasesCommand(1));
		deploySampleApp();

		try {
			try {
				initializeAppDeployer(new DeployOtherDatabasesCommand(1));
				appDeployer.undeploy(appConfig);
				fail("Undeploying should have failed because the database is attached to the REST server");
			} catch (RuntimeException ex) {
				logger.info("Caught expected exception: " + ex.getMessage());
			}

			appConfig.setCatchUndeployExceptions(true);
			// This should just log the error and not throw one
			appDeployer.undeploy(appConfig);
		} finally {
			initializeAppDeployer(new DeployRestApiServersCommand(true), new DeployOtherDatabasesCommand());
			appDeployer.undeploy(appConfig);
		}
	}

	@Test
	public void ignoreErrorOnDeploy() {
		appConfig.setResourceFilenamesIncludePattern(Pattern.compile("content.*.json"));
		initializeAppDeployer(new DeployOtherDatabasesCommand(1));

		try {
			deploySampleApp();
			fail("Deploying should have failed because the content database refers to a non-existent schemas database");
		} catch (Exception ex) {
			logger.info("Caught expected exception: " + ex.getMessage());
		}

		appConfig.setCatchDeployExceptions(true);
		// The error should just be logged
		deploySampleApp();

		// Nothing to undeploy!
	}
}
