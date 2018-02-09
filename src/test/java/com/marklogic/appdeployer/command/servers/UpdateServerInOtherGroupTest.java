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

	/**
	 * Creates two servers with the same name, one in Default and one in the other group.
	 */
	@Test
	public void test() {
		appConfig.setConfigDir(new ConfigDir(new File("src/test/resources/sample-app/other-group")));

		appConfig.setGroupName("Default");

		final String otherGroup = "sample-app-other-group";
		final String serverName = "sample-app-other-server";

		GroupManager groupManager = new GroupManager(manageClient);
		ServerManager defaultServerManager = new ServerManager(manageClient);
		ServerManager otherServerManager = new ServerManager(manageClient, otherGroup);

		initializeAppDeployer(new DeployGroupsCommand(), new DeployOtherServersCommand());

		try {
			deploySampleApp();

			assertTrue(groupManager.exists(otherGroup));
			assertTrue(defaultServerManager.exists(serverName));
			assertTrue(otherServerManager.exists(serverName));

			// Now deploy it again, verifying no errors - i.e. that the other group name is included correctly in the call to update the server
			deploySampleApp();

			assertTrue(groupManager.exists(otherGroup));
			assertTrue(defaultServerManager.exists(serverName));
			assertTrue(otherServerManager.exists(serverName));

		} finally {
			undeploySampleApp();
		}
	}

}
