package com.marklogic.appdeployer.command.cma;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.appservers.DeployOtherServersCommand;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.appservers.ServerManager;
import org.junit.After;
import org.junit.Test;

import java.io.File;

public class DeployServersWithCmaTest extends AbstractAppDeployerTest {

	@After
	public void teardown() {
		initializeAppDeployer(new DeployOtherServersCommand());
		undeploySampleApp();
	}

	@Test
	public void withCma() {
		appConfig.getCmaConfig().setDeployServers(true);
		appConfig.setConfigDir(new ConfigDir(new File("src/test/resources/sample-app/other-servers")));

		ServerManager mgr = new ServerManager(manageClient);

		initializeAppDeployer(new TestDeployOtherServersCommand());

		deploySampleApp();
		assertTrue(mgr.exists("sample-app-odbc"));
		assertTrue(mgr.exists("sample-app-xdbc"));

		deploySampleApp();
		assertTrue(mgr.exists("sample-app-odbc"));
		assertTrue(mgr.exists("sample-app-xdbc"));
	}

}

class TestDeployOtherServersCommand extends DeployOtherServersCommand {

	@Override
	protected ResourceManager getResourceManager(CommandContext context) {
		// This should force an error if CMA isn't used
		return null;
	}
}
