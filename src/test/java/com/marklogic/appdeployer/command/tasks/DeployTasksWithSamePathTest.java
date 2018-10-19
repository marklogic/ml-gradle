package com.marklogic.appdeployer.command.tasks;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.mgmt.resource.tasks.TaskManager;
import org.junit.Test;

import java.io.File;

public class DeployTasksWithSamePathTest extends AbstractAppDeployerTest {

	@Test
	public void test() {
		appConfig.getFirstConfigDir().setBaseDir(new File("src/test/resources/sample-app/tasks-with-same-path"));
		initializeAppDeployer(new DeployScheduledTasksCommand());

		new TaskManager(manageClient).deleteAllScheduledTasks();

		TaskManager taskManager = new TaskManager(manageClient);
		final int initialTaskCount = taskManager.getAsXml().getResourceCount();

		try {
			deploySampleApp();
			assertEquals("There should be 2 new tasks", initialTaskCount + 2, taskManager.getAsXml().getResourceCount());

			deploySampleApp();
			assertEquals("There should still be just 2 new tasks", initialTaskCount + 2, taskManager.getAsXml().getResourceCount());
		} finally {
			undeploySampleApp();

			assertEquals("The 2 new tasks should have been deleted", initialTaskCount, taskManager.getAsXml().getResourceCount());
		}
	}
}
