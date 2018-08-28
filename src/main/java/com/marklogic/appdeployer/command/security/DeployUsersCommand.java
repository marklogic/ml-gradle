package com.marklogic.appdeployer.command.security;

import com.marklogic.appdeployer.command.AbstractResourceCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.api.API;
import com.marklogic.mgmt.api.security.User;
import com.marklogic.mgmt.mapper.DefaultResourceMapper;
import com.marklogic.mgmt.mapper.ResourceMapper;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.security.UserManager;

import java.io.File;

/**
 * As of version 3.9.0, this will now use CMA to deploy users if isOptimizeWithCma on the AppConfig object passed in
 * via the CommandContext returns true.
 */
public class DeployUsersCommand extends AbstractResourceCommand {

	public DeployUsersCommand() {
		setExecuteSortOrder(SortOrderConstants.DEPLOY_USERS);
		setUndoSortOrder(SortOrderConstants.DELETE_USERS);
	}

	protected File[] getResourceDirs(CommandContext context) {
		return findResourceDirs(context.getAppConfig(), configDir -> configDir.getUsersDir());
	}

	@Override
	protected ResourceManager getResourceManager(CommandContext context) {
		return new UserManager(context.getManageClient());
	}

	@Override
	protected void processExecuteOnResourceDir(CommandContext context, File resourceDir) {
		if (context.getAppConfig().isOptimizeWithCma()) {
			deployUsersViaCma(context, resourceDir);
		} else {
			super.processExecuteOnResourceDir(context, resourceDir);
		}
	}

	/**
	 * If deploying via CMA, then each user file is read in, unmarshalled into a User object, and then written out as
	 * JSON. This allows for both JSON and XML files to be supported.
	 *
	 * @param context
	 * @param resourceDir
	 */
	protected void deployUsersViaCma(CommandContext context, File resourceDir) {
		if (resourceDir.exists()) {
			ResourceMapper resourceMapper = new DefaultResourceMapper(new API(context.getManageClient()));

			StringBuilder sb = new StringBuilder("{\"config\":[{\"user\":[");

			boolean foundUser = false;
			for (File f : listFilesInDirectory(resourceDir, context)) {
				if (logger.isInfoEnabled()) {
					logger.info("Processing file: " + f.getAbsolutePath());
				}
				String payload = copyFileToString(f, context);
				User user = resourceMapper.readResource(payload, User.class);
				if (foundUser) {
					sb.append(",");
				}
				sb.append(user.getJson());
				foundUser = true;
			}

			if (foundUser) {
				sb.append("]}]}");

				// Not logging the payload because it can contain passwords
				if (logger.isInfoEnabled()) {
					logger.info("Submitting configuration containing users");
				}
				context.getManageClient().postJson("/manage/v3", sb.toString());
				if (logger.isInfoEnabled()) {
					logger.info("Successfully submitted configuration containing users");
				}
			}
		} else {
			logResourceDirectoryNotFound(resourceDir);
		}
	}
}
