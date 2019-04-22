package com.marklogic.appdeployer.command.restapis;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.command.databases.DeployOtherDatabasesCommand;
import com.marklogic.mgmt.resource.restapis.RestApiManager;
import org.junit.Test;

import java.io.File;

public class DontCreateRestApiTest extends AbstractAppDeployerTest {

	@Test
	public void test() {
		appConfig.getFirstConfigDir().setBaseDir(new File("src/test/resources/sample-app/db-only-config"));
		appConfig.setNoRestServer(true);

		initializeAppDeployer(new DeployRestApiServersCommand(), new DeployOtherDatabasesCommand(1));

		try {
			appDeployer.deploy(appConfig);
			RestApiManager mgr = new RestApiManager(manageClient);
			assertFalse("A REST API server should not have been created",
				mgr.restApiServerExists(appConfig.getRestServerName()));
		} finally {
			undeploySampleApp();
		}
	}
}
