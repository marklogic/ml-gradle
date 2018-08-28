package com.marklogic.appdeployer.command.restapis;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.command.groups.DeployGroupsCommand;
import com.marklogic.mgmt.resource.appservers.ServerManager;
import org.junit.After;
import org.junit.Test;

import java.io.File;

public class CreateRestApiInOtherGroupTest extends AbstractAppDeployerTest {

	@After
	public void tearDown() {
		undeploySampleApp();
	}

	@Test
	public void test() {
		final String groupName = "ml-app-deployer-other-group";
		final String serverName = appConfig.getRestServerName();

		appConfig.setGroupName(groupName);
		appConfig.getFirstConfigDir().setBaseDir(new File("src/test/resources/sample-app/rest-api-other-group"));
		initializeAppDeployer(new DeployGroupsCommand(), new DeployRestApiServersCommand());

		deploySampleApp();
		assertTrue(new ServerManager(manageClient, groupName).exists(serverName));
		assertFalse(new ServerManager(manageClient).exists(serverName));

		deploySampleApp();
		assertTrue("The deployment should have succeeded because RestApiManager now checks to see if the REST API server " +
				"exists in the group defined by appConfig.getGroupName as opposed to just the Default group",
			new ServerManager(manageClient, groupName).exists(serverName));
		assertFalse(new ServerManager(manageClient).exists(serverName));
	}
}
