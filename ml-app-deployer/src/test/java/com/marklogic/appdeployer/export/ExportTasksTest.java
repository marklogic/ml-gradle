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
package com.marklogic.appdeployer.export;

import com.marklogic.appdeployer.command.tasks.DeployScheduledTasksCommand;
import com.marklogic.mgmt.selector.PrefixResourceSelector;
import com.marklogic.mgmt.selector.ResourceSelection;
import com.marklogic.mgmt.resource.tasks.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ExportTasksTest extends AbstractExportTest {

	@AfterEach
	public void teardown() {
		undeploySampleApp();
	}

	@Test
	public void test() {
		initializeAppDeployer(new DeployScheduledTasksCommand());
		deploySampleApp();

		String taskPath = "/path/to/query.xqy";

		// Get some test coverage of selectors, even though we don't care about users here
		PrefixResourceSelector selector = new PrefixResourceSelector("/path");
		selector.setIncludeTypesAsString(ResourceSelection.TASKS + "," + ResourceSelection.USERS);

		ExportedResources resources = new Exporter(manageClient, "Default").select(selector).export(exportDir);
		assertEquals(1, resources.getFiles().size());
		assertEquals("query.xqy.json", resources.getFiles().get(0).getName());

		TaskManager mgr = new TaskManager(manageClient);
		mgr.deleteTaskWithPath(taskPath, "/");
		assertFalse(mgr.exists(taskPath));

		appConfig.getFirstConfigDir().setBaseDir(exportDir);
		deploySampleApp();
		assertTrue(mgr.exists(taskPath));

		mgr.deleteTaskWithPath(taskPath, "/");
		assertFalse(mgr.exists(taskPath));
	}
}
