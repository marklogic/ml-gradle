/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.command.mimetypes;

import com.marklogic.appdeployer.command.AbstractResourceCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.ResourceReference;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.SaveReceipt;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.mimetypes.MimetypeManager;

import java.io.File;

public class DeployMimetypesCommand extends AbstractResourceCommand {

	public DeployMimetypesCommand() {
		setExecuteSortOrder(SortOrderConstants.DEPLOY_MIMETYPES);
		setUndoSortOrder(SortOrderConstants.DELETE_MIMETYPES);
		setRestartAfterDelete(true);
	}

	@Override
	protected File[] getResourceDirs(CommandContext context) {
		return findResourceDirs(context, configDir -> configDir.getMimetypesDir());
	}

	@Override
	protected ResourceManager getResourceManager(CommandContext context) {
		MimetypeManager mgr = new MimetypeManager(context.getManageClient());
		if (context.getAppConfig().isUpdateMimetypeWhenPropertiesAreEqual()) {
			mgr.setUpdateWhenPropertiesAreEqual(true);
		} else {
			mgr.setUpdateWhenPropertiesAreEqual(false);
		}
		return mgr;
	}

	/**
	 * As of ML 8.0-4, any time a mimetype is created or updated, ML must be restarted.
	 * <p>
	 * In ml-app-deployer 3.8.1 though, a restart won't occur on an update if the mimetype properties have not changed.
	 */
	@Override
	protected void afterResourceSaved(ResourceManager mgr, CommandContext context, ResourceReference resourceReference,
	                                  SaveReceipt receipt) {
		if (receipt != null && receipt.hasLocationHeader()) {
			logger.info("Waiting for restart after saving mimetype");
			context.getAdminManager().waitForRestart();
		}
	}

}
