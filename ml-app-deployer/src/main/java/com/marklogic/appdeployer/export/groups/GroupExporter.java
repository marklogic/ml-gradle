/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.export.groups;

import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.export.impl.AbstractNamedResourceExporter;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.groups.GroupManager;

import java.io.File;

public class GroupExporter extends AbstractNamedResourceExporter {

	public GroupExporter(ManageClient manageClient, String... resourceNames) {
		super(manageClient, resourceNames);
	}

	@Override
	protected ResourceManager newResourceManager(ManageClient manageClient) {
		return new GroupManager(manageClient);
	}

	@Override
	protected File getResourceDirectory(File baseDir) {
		return new ConfigDir(baseDir).getGroupsDir();
	}
}
