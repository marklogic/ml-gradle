package com.marklogic.appdeployer.command.modules;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.databases.DeployContentDatabasesCommand;
import com.marklogic.appdeployer.command.restapis.DeployRestApiServersCommand;
import com.marklogic.client.ext.modulesloader.impl.DefaultModulesLoader;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;

public class LoadInvalidRestModulesTest extends AbstractAppDeployerTest {

	@Test
	public void test() {
		appConfig.getFirstConfigDir().setBaseDir(new File("src/test/resources/sample-app/db-only-config"));

		appConfig.setModulePaths(Arrays.asList("src/test/resources/sample-app/bad-modules"));

		LoadModulesCommand loadModulesCommand = new LoadModulesCommand();
		loadModulesCommand.initializeDefaultModulesLoader(new CommandContext(appConfig, manageClient, null));
		DefaultModulesLoader modulesLoader = (DefaultModulesLoader) loadModulesCommand.getModulesLoader();
		modulesLoader.setModulesManager(null);

		initializeAppDeployer(new DeployRestApiServersCommand(), new DeployContentDatabasesCommand(1), loadModulesCommand);

		try {
			deploySampleApp();
			fail("The invalid search options file should have caused the LoadModulesCommand to fail");
		} catch (RuntimeException re) {
			logger.info("Caught expected error: " + re.getMessage());
			assertTrue(re.getMessage().contains("Unexpected character"));
		} finally {
			undeploySampleApp();
		}
	}
}
