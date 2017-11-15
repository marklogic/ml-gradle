package com.marklogic.appdeployer.command.servers;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.appservers.DeployOtherServersCommand;
import com.marklogic.appdeployer.command.groups.DeployGroupsCommand;
import com.marklogic.mgmt.resource.appservers.ServerManager;
import com.marklogic.mgmt.resource.groups.GroupManager;
import org.junit.Test;

import java.io.File;

public class UpdateServerInOtherGroupTest extends AbstractAppDeployerTest {

	@Test
	public void test() {
		appConfig.setConfigDir(new ConfigDir(new File("src/test/resources/sample-app/other-group")));

		appConfig.setGroupName("Default");

		final String otherGroup = "sample-app-other-group";
		final String otherServer = "sample-app-other-server";

		GroupManager groupManager = new GroupManager(manageClient);
		ServerManager serverManager = new ServerManager(manageClient);

		initializeAppDeployer(new DeployGroupsCommand(), new DeployOtherServersCommand());

		try {
			deploySampleApp();

			assertTrue(groupManager.exists(otherGroup));
			assertTrue(serverManager.exists(otherServer));

			// Now deploy it again, verifying no errors - i.e. that the other group name is included correctly in the call to update the server
			deploySampleApp();

			assertTrue(groupManager.exists(otherGroup));
			assertTrue(serverManager.exists(otherServer));

		} finally {
			undeploySampleApp();
		}
	}

}
