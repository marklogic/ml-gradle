package com.marklogic.appdeployer.command.databases;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.triggers.DeployTriggersCommand;
import com.marklogic.mgmt.resource.triggers.TriggerManager;
import org.junit.After;
import org.junit.Test;

import java.io.File;

public class DeployTriggersToMultipleDatabasesTest extends AbstractAppDeployerTest {

	@After
	public void teardown() {
		undeploySampleApp();
	}

	@Test
	public void test() {
		appConfig.setConfigDir(new ConfigDir(new File("src/test/resources/sample-app/multiple-triggers-databases")));

		initializeAppDeployer(new DeployOtherDatabasesCommand(1), new DeployTriggersCommand());

		deploySampleApp();

		TriggerManager triggerManager = new TriggerManager(manageClient, appConfig.getTriggersDatabaseName());
		assertTrue(triggerManager.exists("my-trigger"));

		triggerManager = new TriggerManager(manageClient, "other-" + appConfig.getTriggersDatabaseName());
		assertTrue(triggerManager.exists("other-trigger"));
	}
}
