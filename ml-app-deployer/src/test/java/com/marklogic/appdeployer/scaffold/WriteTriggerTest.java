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

import com.marklogic.appdeployer.command.databases.DeployOtherDatabasesCommand;
import com.marklogic.appdeployer.command.triggers.DeployTriggersCommand;
import com.marklogic.mgmt.api.trigger.Trigger;
import com.marklogic.mgmt.resource.triggers.TriggerManager;
import com.marklogic.mgmt.template.database.DatabaseTemplateBuilder;
import com.marklogic.mgmt.template.trigger.TriggerTemplateBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WriteTriggerTest extends AbstractResourceWriterTest {

	@Test
	public void defaultValues() {
		initializeAppDeployer(new DeployOtherDatabasesCommand(1), new DeployTriggersCommand());

		final String triggersDatabase = appConfig.getTriggersDatabaseName();

		buildResourceAndDeploy(new DatabaseTemplateBuilder(triggersDatabase));
		buildResourceAndDeploy(new TriggerTemplateBuilder(triggersDatabase));

		TriggerManager mgr = new TriggerManager(manageClient, triggersDatabase);
		assertTrue(mgr.exists("trigger-name"));

		Trigger t = api.trigger("trigger-name", triggersDatabase);
		assertEquals("trigger-name", t.getName());
		assertEquals("Trigger description", t.getDescription());
		assertEquals("some-collection", t.getEvent().getDataEvent().getCollectionScope().getUri());
		assertEquals("create", t.getEvent().getDataEvent().getDocumentContent().getUpdateKind());
		assertEquals("pre-commit", t.getEvent().getDataEvent().getWhen());
		assertEquals("/path/to/module.sjs", t.getModule());
		assertEquals("Modules", t.getModuleDb());
		assertEquals("/", t.getModuleRoot());
		assertTrue(t.getEnabled());
		assertFalse(t.getRecursive());
		assertEquals("normal", t.getTaskPriority());
	}
}
