package com.marklogic.appdeployer.command.tasks;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.mgmt.resource.tasks.TaskManager;
import com.marklogic.rest.util.PreviewInterceptor;
import org.junit.After;
import org.junit.Test;

import java.io.File;

public class DeployTasksWithSamePathTest extends AbstractAppDeployerTest {

	@After
	public void after() {
		new TaskManager(manageClient).deleteAllScheduledTasks();
	}

	@Test
	public void test() {
		appConfig.getFirstConfigDir().setBaseDir(new File("src/test/resources/sample-app/tasks-with-same-path"));
		initializeAppDeployer(new DeployScheduledTasksCommand());

		new TaskManager(manageClient).deleteAllScheduledTasks();

		TaskManager taskManager = new TaskManager(manageClient);
		final int initialTaskCount = taskManager.getAsXml().getResourceCount();

		try {
			deploySampleApp();
			assertEquals("There should be 3 new tasks; task-4.json should overwrite task 3 because it has the same " +
				"database, module path, and task root as task-3.json", initialTaskCount + 3, taskManager.getAsXml().getResourceCount());

			// Verify we get an expected error when trying to get a single task by a task-path
			try {
				taskManager.getTaskIdForTaskPath("/path/to/query.xqy");
				fail("Expected an error because getTaskIdForTaskPath can't return a single ID when multiple tasks exist with the same path");
			} catch (Exception ex) {
				logger.info("Caught expected error because multiple tasks exist with the same path: " + ex.getMessage());
			}

			deploySampleApp();
			assertEquals("There should still be just 3 new tasks", initialTaskCount + 3, taskManager.getAsXml().getResourceCount());

			// test preview. No tasks are accidentally deleted
			PreviewInterceptor interceptor = new PreviewInterceptor(manageClient);
			manageClient.getRestTemplate().getInterceptors().add(interceptor);

			deploySampleApp();
			assertEquals("There should still be just 3 new tasks", initialTaskCount + 3, taskManager.getAsXml().getResourceCount());

		} finally {
			// we want to undeploy the sample app not just preview it
			manageClient.getRestTemplate().getInterceptors().clear();
			undeploySampleApp();

			assertEquals("The 3 new tasks should have been deleted", initialTaskCount, taskManager.getAsXml().getResourceCount());
		}
	}

}
