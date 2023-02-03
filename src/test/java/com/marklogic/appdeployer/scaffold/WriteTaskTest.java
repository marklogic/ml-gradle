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
