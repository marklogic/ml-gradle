package com.marklogic.appdeployer.command.restapis;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.appservers.UpdateRestApiServersCommand;
import org.junit.After;
import org.junit.Test;
import org.springframework.web.client.HttpClientErrorException;

import java.io.File;

public class CreateRestApiWithCustomRewriterTest extends AbstractAppDeployerTest {

	@After
	public void teardown() {
		undeploySampleApp();
	}

	/**
	 * This test is used to manually inspect the log statements that are written.
	 */
	@Test
	public void test() {
		appConfig.setConfigDir(new ConfigDir(new File("src/test/resources/sample-app/rest-api-different-rewriter")));

		initializeAppDeployer(new DeployRestApiServersCommand(), new UpdateRestApiServersCommand());

		try {
			deploySampleApp();
		} catch (HttpClientErrorException ex) {
			logger.info("Caught expected exception because the REST server has a url-rewriter without 'rest-api' in it: " + ex.getMessage());
		}
	}
}
