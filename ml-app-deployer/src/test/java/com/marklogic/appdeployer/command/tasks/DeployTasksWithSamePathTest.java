/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.appdeployer.command.tasks;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.mgmt.resource.tasks.TaskManager;
import com.marklogic.rest.util.PreviewInterceptor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class DeployTasksWithSamePathTest extends AbstractAppDeployerTest {

	@AfterEach
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
			assertEquals(initialTaskCount + 3, taskManager.getAsXml().getResourceCount(),
				"There should be 3 new tasks; task-4.json should overwrite task 3 because it has the same " +
					"database, module path, and task root as task-3.json");

			// Verify we get an expected error when trying to get a single task by a task-path
			try {
				taskManager.getTaskIdForTaskPath("/path/to/query.xqy");
				fail("Expected an error because getTaskIdForTaskPath can't return a single ID when multiple tasks exist with the same path");
			} catch (Exception ex) {
				logger.info("Caught expected error because multiple tasks exist with the same path: " + ex.getMessage());
			}

			deploySampleApp();
			assertEquals(initialTaskCount + 3, taskManager.getAsXml().getResourceCount(), "There should still be just 3 new tasks");

			// test preview. No tasks are accidentally deleted
			PreviewInterceptor interceptor = new PreviewInterceptor(manageClient);
			manageClient.getRestTemplate().getInterceptors().add(interceptor);

			deploySampleApp();
			assertEquals(initialTaskCount + 3, taskManager.getAsXml().getResourceCount(), "There should still be just 3 new tasks");

		} finally {
			// we want to undeploy the sample app not just preview it
			manageClient.getRestTemplate().getInterceptors().clear();
			undeploySampleApp();

			assertEquals(initialTaskCount, taskManager.getAsXml().getResourceCount(), "The 3 new tasks should have been deleted");
		}
	}

}
