package com.marklogic.appdeployer.command.databases;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.triggers.DeployTriggersCommand;
import com.marklogic.mgmt.resource.triggers.TriggerManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DeployTriggersToMultipleDatabasesTest extends AbstractAppDeployerTest {

	@AfterEach
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

		triggerManager = new TriggerManager(manageClient, "third-" + appConfig.getTriggersDatabaseName());
		assertTrue(triggerManager.exists("third-trigger"));
	}
}
