package com.marklogic.appdeployer.command.servers;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.appservers.DeployOtherServersCommand;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;

public class WaitForRestartWhenUpdatingServerTest extends AbstractAppDeployerTest {

	@AfterEach
	public void tearDown() {
		undeploySampleApp();
	}

	@Test
	public void test() {
		appConfig.setConfigDir(new ConfigDir(new File("src/test/resources/sample-app/single-server")));
		initializeAppDeployer(new DeployOtherServersCommand());
		appConfig.getCustomTokens().put("%%HTTP_PORT%%", "8048");
		appDeployer.deploy(appConfig);

		// Now change the port, and then redeploy, and immediately deploy again to verify that the redeploy waits for
		// ML to restart
		appConfig.getCustomTokens().put("%%HTTP_PORT%%", "8049");
		appDeployer.deploy(appConfig);
		appDeployer.deploy(appConfig);

		// Nothing to verify - the lack of an error means that the command waited for ML to restart
	}
}
