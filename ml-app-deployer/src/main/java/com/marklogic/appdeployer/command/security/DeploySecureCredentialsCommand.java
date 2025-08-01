/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.command.security;

import com.marklogic.appdeployer.command.AbstractResourceCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.security.SecureCredentialsManager;

import java.io.File;

public class DeploySecureCredentialsCommand extends AbstractResourceCommand {

	public DeploySecureCredentialsCommand() {
		setExecuteSortOrder(SortOrderConstants.DEPLOY_SECURE_CREDENTIALS);
		setUndoSortOrder(SortOrderConstants.DEPLOY_SECURE_CREDENTIALS);
	}

	@Override
	protected File[] getResourceDirs(CommandContext context) {
		return findResourceDirs(context, configDir -> configDir.getSecureCredentialsDir());
	}

	@Override
	protected ResourceManager getResourceManager(CommandContext context) {
		return new SecureCredentialsManager(context.getManageClient());
	}
}
