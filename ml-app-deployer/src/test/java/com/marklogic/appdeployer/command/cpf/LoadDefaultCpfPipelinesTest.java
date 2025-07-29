/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.command.cpf;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.command.databases.DeployOtherDatabasesCommand;
import com.marklogic.mgmt.resource.cpf.PipelineManager;
import com.marklogic.rest.util.ResourcesFragment;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoadDefaultCpfPipelinesTest extends AbstractAppDeployerTest {

	@AfterEach
	void teardown() {
		undeploySampleApp();
	}

	@Test
	void loadDefaultCpfPipelines() {
		initializeAppDeployer(new DeployOtherDatabasesCommand(1));

		appDeployer.deploy(appConfig);

		String dbName = appConfig.getTriggersDatabaseName();

		PipelineManager mgr = new PipelineManager(manageClient, dbName);
		mgr.loadDefaultPipelines();

		ResourcesFragment f = mgr.getAsXml();
		assertEquals(16, f.getResourceCount(), "As of MarkLogic 12, 16 pipelines should  be loaded by default. " +
			"This is a change from previous versions, where 23 were loaded by default. ");
	}
}
