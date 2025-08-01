/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.command.security;

import com.marklogic.appdeployer.command.AbstractResourceCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.appdeployer.command.UndoableCommand;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.security.CredentialsManager;

import java.io.File;

public class DeployCredentialsCommand extends AbstractResourceCommand implements UndoableCommand {

	public DeployCredentialsCommand() {
		setExecuteSortOrder(SortOrderConstants.DEPLOY_CREDENTIALS);
		setUndoSortOrder(SortOrderConstants.DEPLOY_CREDENTIALS);
	}

	@Override
	protected File[] getResourceDirs(CommandContext context) {
		return findResourceDirs(context, configDir -> configDir.getCredentialsDir());
	}

	@Override
	protected ResourceManager getResourceManager(CommandContext context) {
		return new CredentialsManager(context.getManageClient());
	}
}
