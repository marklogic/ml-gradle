package com.marklogic.appdeployer.command.security;

import com.marklogic.appdeployer.command.AbstractResourceCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.api.API;
import com.marklogic.mgmt.api.security.Role;
import com.marklogic.mgmt.mapper.DefaultResourceMapper;
import com.marklogic.mgmt.mapper.ResourceMapper;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.security.RoleManager;

import java.io.File;

public class DeployRolesCommand extends AbstractResourceCommand {

	// Used internally
	private boolean removeRolesAndPermissionsDuringDeployment = false;
	private ResourceMapper resourceMapper;

	public DeployRolesCommand() {
		setExecuteSortOrder(SortOrderConstants.DEPLOY_ROLES);
		setUndoSortOrder(SortOrderConstants.DELETE_ROLES);
	}

	/**
	 * The set of roles is processed twice. The first time, the roles are saved without any permissions or dependent roles.
	 * This is to avoid issues where the roles depend on each other or on themselves. The second time, the roles are
	 * saved with permissions and dependent roles, which is guaranteed to work now that the roles have all been created.
	 *
	 * @param context
	 */
	@Override
	public void execute(CommandContext context) {
		removeRolesAndPermissionsDuringDeployment = true;
		if (logger.isInfoEnabled()) {
			logger.info("Deploying roles without any permissions or dependent roles");
		}
		super.execute(context);
		if (logger.isInfoEnabled()) {
			logger.info("Deploying roles with permissions and dependent roles");
		}
		removeRolesAndPermissionsDuringDeployment = false;
		super.execute(context);
	}

	@Override
	protected String adjustPayloadBeforeSavingResource(ResourceManager mgr, CommandContext context, File f, String payload) {
		if (removeRolesAndPermissionsDuringDeployment) {
			if (resourceMapper == null) {
				API api = new API(context.getManageClient(), context.getAdminManager());
				resourceMapper = new DefaultResourceMapper(api);
			}
			Role role = resourceMapper.readResource(payload, Role.class);
			role.clearPermissionsAndRoles();
			return role.getJson();
		}
		return payload;
	}

	protected File[] getResourceDirs(CommandContext context) {
		return new File[]{context.getAppConfig().getConfigDir().getRolesDir()};
	}

	@Override
	protected ResourceManager getResourceManager(CommandContext context) {
		return new RoleManager(context.getManageClient());
	}
}

