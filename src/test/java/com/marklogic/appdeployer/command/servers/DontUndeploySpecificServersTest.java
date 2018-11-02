package com.marklogic.appdeployer.command.servers;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.appservers.DeployOtherServersCommand;
import com.marklogic.mgmt.resource.appservers.ServerManager;
import org.junit.Test;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class DontUndeploySpecificServersTest extends AbstractAppDeployerTest {

	@Test
	public void test() {
		appConfig.setConfigDir(new ConfigDir(new File("src/test/resources/sample-app/other-servers")));

		ServerManager mgr = new ServerManager(manageClient);

		DeployOtherServersCommand c = new DeployOtherServersCommand();
		initializeAppDeployer(c);

		appConfig.getCustomTokens().put("%%ODBC_PORT%%", "8048");
		appConfig.getCustomTokens().put("%%XDBC_PORT%%", "8049");
		deploySampleApp();

		try {
			assertTrue(mgr.exists("sample-app-xdbc"));
			assertTrue(mgr.exists("sample-app-odbc"));

			appConfig.setServersToNotUndeploy("sample-app-xdbc", "sample-app-odbc");
			undeploySampleApp();
			assertTrue(mgr.exists("sample-app-xdbc"));
			assertTrue(mgr.exists("sample-app-odbc"));
		} finally {
			appConfig.setServersToNotUndeploy(null);
			undeploySampleApp();
			assertFalse(mgr.exists("sample-app-xdbc"));
			assertFalse(mgr.exists("sample-app-odbc"));
		}
	}

	@Test
	public void unitTestShouldUndeployServer() {
		DeployOtherServersCommand c = new DeployOtherServersCommand();

		AppConfig appConfig = new AppConfig();
		CommandContext context = new CommandContext(appConfig, null, null);

		// These should all be not-undeployed by default
		assertFalse(c.shouldUndeployServer("Admin", null));
		assertFalse(c.shouldUndeployServer("App-Services", null));
		assertFalse(c.shouldUndeployServer("HealthCheck", null));
		assertFalse(c.shouldUndeployServer("Manage", null));

		appConfig.setServersToNotUndeploy("server1", "server2");
		assertFalse(c.shouldUndeployServer("server1", context));
		assertFalse(c.shouldUndeployServer("server2", context));
		assertTrue(c.shouldUndeployServer("server3", context));

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
