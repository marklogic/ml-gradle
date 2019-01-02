package com.marklogic.appdeployer.command.servers;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.appservers.DeployOtherServersCommand;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.appservers.ServerManager;
import org.junit.Test;

import java.io.File;

public class DeployServersWithCmaTest extends AbstractAppDeployerTest {

	@Test
	public void test() {
		initializeAppDeployer(new TestDeployServersCommand());

		appConfig.setConfigDir(new ConfigDir(new File("src/test/resources/sample-app/other-servers")));
		appConfig.setDeployServersWithCma(true);

		ServerManager mgr = new ServerManager(manageClient);

		try {
			deploySampleApp();

			assertTrue(mgr.exists("sample-app-odbc"));
			assertTrue(mgr.exists("sample-app-xdbc"));
		} finally {
			initializeAppDeployer(new DeployOtherServersCommand());
			undeploySampleApp();

			assertFalse(mgr.exists("sample-app-odbc"));
			assertFalse(mgr.exists("sample-app-xdbc"));
		}
	}
}

class TestDeployServersCommand extends DeployOtherServersCommand {

	@Override
	protected ResourceManager getResourceManager(CommandContext context) {
		throw new RuntimeException("This shouldn't be called when CMA is used");
	}
}
