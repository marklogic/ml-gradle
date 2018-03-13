package com.marklogic.appdeployer.command.restapis;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.appservers.UpdateRestApiServersCommand;
import com.marklogic.mgmt.api.API;
import org.junit.After;
import org.junit.Test;

import java.io.File;

public class CreateRestApiWithCustomRewriterTest extends AbstractAppDeployerTest {

	@After
	public void teardown() {
		undeploySampleApp();
	}

	/**
	 * This was created for #242, but is being modified for #256 as this library is now using ServerManager instead
	 * of /v1/rest-apis to see if a REST server exists already.
	 */
	@Test
	public void test() {
		appConfig.setConfigDir(new ConfigDir(new File("src/test/resources/sample-app/rest-api-different-rewriter")));

		initializeAppDeployer(new DeployRestApiServersCommand(true), new UpdateRestApiServersCommand());

		deploySampleApp();
		assertEquals("/my/custom/rewriter.xml", new API(manageClient).server("sample-app").getUrlRewriter());

		// This shouldn't throw an error, because it's no longer using /v1/rest-apis to see if the REST server exists
		deploySampleApp();
		assertEquals("/my/custom/rewriter.xml", new API(manageClient).server("sample-app").getUrlRewriter());
	}
}
