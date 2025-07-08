/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.scaffold;

import com.marklogic.appdeployer.command.databases.DeployOtherDatabasesCommand;
import com.marklogic.appdeployer.command.triggers.DeployTriggersCommand;
import com.marklogic.mgmt.api.trigger.Trigger;
import com.marklogic.mgmt.resource.databases.DatabaseManager;
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
		assertEquals("/", t.getModuleRoot());
		assertTrue(t.getEnabled());
		assertFalse(t.getRecursive());
		assertEquals("normal", t.getTaskPriority());

		String modulesId = new DatabaseManager(manageClient).getAsXml().getIdForNameOrId("Modules");
		assertEquals(modulesId, t.getModuleDb());
	}
}
