/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt;

import com.marklogic.client.ext.helper.LoggingObject;
import com.marklogic.junit5.MarkLogicVersion;
import com.marklogic.junit5.MarkLogicVersionSupplier;
import com.marklogic.mgmt.admin.AdminConfig;
import com.marklogic.mgmt.admin.AdminManager;
import com.marklogic.mgmt.resource.clusters.ClusterManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Base class for tests that just talk to the Mgmt API and don't depend on an AppDeployer instance.
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfig.class})
public abstract class AbstractMgmtTest extends LoggingObject implements MarkLogicVersionSupplier {

	@Autowired
	protected ManageConfig manageConfig;

	@Autowired
	protected AdminConfig adminConfig;

	// Intended to be used by subclasses
	protected ManageClient manageClient;
	protected AdminManager adminManager;

	@BeforeEach
	public void initializeManageClient() {
		if (manageClient == null) {
			manageClient = new ManageClient(manageConfig);
		}
		adminManager = new AdminManager(adminConfig);
	}

	/**
	 * Allows for marklogic-junit5 annotations to be used that control test execution based on the MarkLogic version.
	 *
	 * @return
	 */
	@Override
	public MarkLogicVersion getMarkLogicVersion() {
		if (manageClient == null) {
			manageClient = new ManageClient(manageConfig);
		}
		String version = new ClusterManager(manageClient).getVersion();
		return new MarkLogicVersion(version);
	}
}
