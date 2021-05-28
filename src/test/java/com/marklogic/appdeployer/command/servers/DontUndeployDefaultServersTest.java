package com.marklogic.appdeployer.command.servers;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.appservers.DeployOtherServersCommand;
import com.marklogic.mgmt.resource.appservers.ServerManager;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DontUndeployDefaultServersTest extends AbstractAppDeployerTest {

	@Test
	public void test() {
		// CMA doesn't like the minimal Manage server file
		appConfig.getCmaConfig().setDeployServers(false);

		appConfig.setConfigDir(new ConfigDir(new File("src/test/resources/sample-app/default-servers")));

		ServerManager mgr = new ServerManager(manageClient);

		DeployOtherServersCommand c = new DeployOtherServersCommand();
		initializeAppDeployer(c);

		deploySampleApp();

		assertTrue(mgr.exists("sample-app-xdbc"));
		assertTrue(mgr.exists("Manage"));

		undeploySampleApp();
		assertFalse(mgr.exists("sample-app-xdbc"));
		assertTrue(mgr.exists("Manage"));
	}

	@Test
	public void unitTestShouldUndeployServer() {
		DeployOtherServersCommand c = new DeployOtherServersCommand();

		// These should all be not-undeployed by default
		assertFalse(c.shouldUndeployServer("Admin", null));
		assertFalse(c.shouldUndeployServer("App-Services", null));
		assertFalse(c.shouldUndeployServer("HealthCheck", null));
		assertFalse(c.shouldUndeployServer("Manage", null));

		Set<String> customDefaultServersNotToUndeploy = new HashSet<>();
		customDefaultServersNotToUndeploy.add("Admin");
		customDefaultServersNotToUndeploy.add("TestServer");
		c.setDefaultServersToNotUndeploy(customDefaultServersNotToUndeploy);

		assertFalse(c.shouldUndeployServer("Admin", null));
		assertFalse(c.shouldUndeployServer("TestServer", null));
		assertTrue(c.shouldUndeployServer("App-Services", null));
		assertTrue(c.shouldUndeployServer("HealthCheck", null));
		assertTrue(c.shouldUndeployServer("Manage", null));
	}
}
