/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.command.security;

import com.marklogic.appdeployer.command.AbstractResourceCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.security.ExternalSecurityManager;

import java.io.File;

public class DeployExternalSecurityCommand extends AbstractResourceCommand {

    public DeployExternalSecurityCommand() {
        setExecuteSortOrder(SortOrderConstants.DEPLOY_EXTERNAL_SECURITY);
        setUndoSortOrder(SortOrderConstants.DELETE_EXTERNAL_SECURITY);
    }

    @Override
    protected File[] getResourceDirs(CommandContext context) {
    	return findResourceDirs(context, configDir -> configDir.getExternalSecuritiesDir());
    }

    @Override
    protected ResourceManager getResourceManager(CommandContext context) {
        return new ExternalSecurityManager(context.getManageClient());
    }

}
