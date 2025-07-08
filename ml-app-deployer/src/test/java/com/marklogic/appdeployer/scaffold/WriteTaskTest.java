/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.scaffold;

import com.marklogic.appdeployer.command.tasks.DeployScheduledTasksCommand;
import com.marklogic.mgmt.api.task.Task;
import com.marklogic.mgmt.mapper.DefaultResourceMapper;
import com.marklogic.mgmt.resource.tasks.TaskManager;
import com.marklogic.mgmt.template.task.TaskTemplateBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WriteTaskTest extends AbstractResourceWriterTest {

	@Test
	public void defaultValues() {
		initializeAppDeployer(new DeployScheduledTasksCommand());

		buildResourceAndDeploy(new TaskTemplateBuilder());

		String json = new TaskManager(manageClient).getPropertiesAsJson("/CHANGEME-path-to-module.sjs", "group-id", "Default");
		System.out.println(json);
		Task task = new DefaultResourceMapper(api).readResource(json, Task.class);
		assertEquals("/CHANGEME-path-to-module.sjs", task.getTaskPath());
		assertEquals("/", task.getTaskRoot());
		assertEquals("daily", task.getTaskType());
		assertEquals(new Integer(1), task.getTaskPeriod());
		assertEquals("01:00:00", task.getTaskStartTime());
		assertEquals("Documents", task.getTaskDatabase());
		assertEquals("Modules", task.getTaskModules());
		assertEquals("admin", task.getTaskUser());
	}
}
