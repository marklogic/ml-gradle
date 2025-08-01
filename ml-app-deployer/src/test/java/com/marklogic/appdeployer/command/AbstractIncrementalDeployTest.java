/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.command;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.mgmt.ManageClient;
import org.junit.jupiter.api.BeforeEach;

public abstract class AbstractIncrementalDeployTest extends AbstractAppDeployerTest {

	protected ManageClient originalManageClient;

	@BeforeEach
	public void setupIncrementalDeployTest() {
		appConfig.setIncrementalDeploy(true);
		this.originalManageClient = this.manageClient;
		deleteResourceTimestampsFile();
	}

	public void deleteResourceTimestampsFile() {
		new ResourceFileManagerImpl().deletePropertiesFile();
	}
}
