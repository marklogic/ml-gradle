/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.command.tasks;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.mgmt.api.API;
import com.marklogic.mgmt.api.task.Task;
import com.marklogic.mgmt.resource.tasks.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class DeployDisabledTaskTest extends AbstractAppDeployerTest {

	private final static String TASK_PATH = "/this/should/be/disabled.xqy";

	@AfterEach
	public void teardown() {
		undeploySampleApp();
		assertFalse(new TaskManager(manageClient).exists(TASK_PATH));
	}

	@Test
	public void test() {
		appConfig.getFirstConfigDir().setBaseDir(new File("src/test/resources/sample-app/tasks-disabled"));
		initializeAppDeployer(new DeployScheduledTasksCommand());

		TaskManager mgr = new TaskManager(manageClient);
		assertFalse(mgr.exists(TASK_PATH));

		deploySampleApp();

		Task task = new API(manageClient).task(mgr.getTaskIdForTaskPath(TASK_PATH));
		assertFalse(task.getTaskEnabled(), "Due to a bug in the Manage API, when a task is first created, task-enabled is ignored " +
			"and the task is always enabled. To work around this, if the payload has task-enabled set to false, " +
			"then a second call should be made to ensure it gets set to false");

		// For ticket #367, make sure that the scheduled task can be updated without throwing an error.
		// This should result in a delete call followed by the task being created again
		task.setTaskModules("Modules");
		mgr.save(task.getJson());

		task = new API(manageClient).task(mgr.getTaskIdForTaskPath(TASK_PATH));
		assertEquals("Modules", task.getTaskModules());
		assertFalse(task.getTaskEnabled(), "The task should still be disabled");
	}
}
