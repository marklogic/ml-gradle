package com.marklogic.appdeployer.impl;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.command.databases.DeployContentDatabasesCommand;
import com.marklogic.appdeployer.command.restapis.DeployRestApiServersCommand;
import org.junit.Test;

import java.io.File;

public class IgnoreCommandErrorTest extends AbstractAppDeployerTest {

	@Test
	public void ignoreErrorOnUndeploy() {
		appConfig.getFirstConfigDir().setBaseDir(new File("src/test/resources/sample-app/db-only-config"));

		initializeAppDeployer(new DeployRestApiServersCommand(true), new DeployContentDatabasesCommand(1));
		deploySampleApp();

		try {
			try {
				initializeAppDeployer(new DeployContentDatabasesCommand());
				appDeployer.undeploy(appConfig);
				fail("Undeploying should have failed because the database is attached to the REST server");
			} catch (RuntimeException ex) {
				logger.info("Caught expected exception: " + ex.getMessage());
			}

			appConfig.setCatchUndeployExceptions(true);
			// This should just log the error and not throw one
			appDeployer.undeploy(appConfig);
		} finally {
			initializeAppDeployer(new DeployRestApiServersCommand(true), new DeployContentDatabasesCommand());
			appDeployer.undeploy(appConfig);
		}
	}

	@Test
	public void ignoreErrorOnDeploy() {
		initializeAppDeployer(new DeployContentDatabasesCommand(1));

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
