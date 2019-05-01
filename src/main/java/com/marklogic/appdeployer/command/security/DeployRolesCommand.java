package com.marklogic.appdeployer.command.security;

import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.appdeployer.command.*;
import com.marklogic.mgmt.SaveReceipt;
import com.marklogic.mgmt.api.API;
import com.marklogic.mgmt.api.configuration.Configuration;
import com.marklogic.mgmt.api.configuration.Configurations;
import com.marklogic.mgmt.api.security.Role;
import com.marklogic.mgmt.mapper.DefaultResourceMapper;
import com.marklogic.mgmt.mapper.ResourceMapper;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.security.RoleManager;
import com.marklogic.mgmt.util.ObjectMapperFactory;
import com.marklogic.rest.util.ResourcesFragment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DeployRolesCommand extends AbstractResourceCommand implements SupportsCmaCommand {

	// Used internally
	private boolean removeRolesAndPermissionsDuringDeployment = false;
	private ResourceMapper resourceMapper;
	private Set<String> roleNamesThatDontNeedToBeRedeployed;

	private boolean deployRolesInTwoPhases = true;

	public DeployRolesCommand() {
		setExecuteSortOrder(SortOrderConstants.DEPLOY_ROLES);
		setUndoSortOrder(SortOrderConstants.DELETE_ROLES);

		setSupportsResourceMerging(true);
		setResourceIdPropertyName("role-name");
		setResourceClassType(Role.class);
	}

	@Override
	public boolean cmaShouldBeUsed(CommandContext context) {
		return context.getAppConfig().getCmaConfig().isDeployRoles();
	}

	@Override
	public void addResourceToConfiguration(ObjectNode resource, Configuration configuration) {
		configuration.addRole(resource);
	}

	/**
	 * The set of roles is processed twice. The first time, the roles are saved without any default permissions or references to other roles.
	 * This is to avoid issues where the roles refer to each other or to themselves (via default permissions). The second time, the roles are
	 * saved with permissions and references to other roles, which is guaranteed to work now that the roles have all been created.
	 * <p>
	 * For 3.11.0, as part of the new preview feature, the boolean deployRolesInTwoPhases has been added so that the
	 * process of deploying roles in two phases can be disabled during a preview.
	 *
	 * @param context
	 */
	@Override
	public void execute(CommandContext context) {
		roleNamesThatDontNeedToBeRedeployed = new HashSet<>();
		if (deployRolesInTwoPhases && !cmaShouldBeUsed(context)) {
			removeRolesAndPermissionsDuringDeployment = true;
			if (logger.isInfoEnabled()) {
				logger.info("Deploying roles minus their default permissions and references to roles");
			}
			super.execute(context);
			if (logger.isInfoEnabled()) {
				logger.info("Redeploying roles that have default permissions and/or references to roles");
			}
			removeRolesAndPermissionsDuringDeployment = false;
		}

		super.execute(context);
	}

	@Override
	protected void deployConfiguration(CommandContext context, Configuration config) {
		submitConfigurationWithRolesThatReferenceThemselves(context, config);

		if (config.getRoles() != null) {
			logger.info("Sorting roles before they are submitted in a CMA request");
			config.setRoles(Role.sortObjectNodes(config.getRoles()));
		}

		if (context.getAppConfig().getCmaConfig().isCombineRequests()) {
			logger.info("Adding roles to combined CMA request");
			context.addCmaConfigurationToCombinedRequest(config);
		} else {
			final String key = getClass().getSimpleName() + "-roles";
			if (removeRolesAndPermissionsDuringDeployment) {
				context.getContextMap().put(key, new Configurations(config));
			} else {
				Configurations configs = (Configurations) context.getContextMap().get(key);
				if (configs == null) {
					// This shouldn't ever happen, but just in case
					configs = new Configurations();
				}
				configs.getConfigs().add(config);
				configs.submit(context.getManageClient());
			}
		}
	}

	/**
	 * If a role refers to itself via permissions, that role won't be created by CMA. Instead, a separate CMA request
	 * is constructed, with each such role only having a role-name, and then immediately submitted so that the roles
	 * are guaranteed to exist. Note that only roles that don't exist yet will be included in this request (if they
	 * already exist, then no problem will occur).
	 *
	 * @param context
	 * @param config
	 */
	protected void submitConfigurationWithRolesThatReferenceThemselves(CommandContext context, Configuration config) {
		List<ObjectNode> roles = config.getRoles();
		if (roles != null && !roles.isEmpty()) {
			ObjectReader reader = ObjectMapperFactory.getObjectMapper().readerFor(Role.class);
			List<ObjectNode> rolesThatReferenceThemselves = new ArrayList<>();
			ResourcesFragment rolesXml = new RoleManager(context.getManageClient()).getAsXml();

			roles.forEach(role -> {
				try {
					Role r = reader.readValue(role);
					if (r.hasPermissionWithOwnRoleName() && !rolesXml.resourceExists(r.getRoleName())) {
						ObjectNode node = ObjectMapperFactory.getObjectMapper().createObjectNode();
						node.put("role-name", r.getRoleName());
						rolesThatReferenceThemselves.add(node);
					}
				} catch (IOException e) {
					throw new RuntimeException("Unable to read ObjectNode into Role; node: " + role, e);
				}
			});

			if (!rolesThatReferenceThemselves.isEmpty()) {
				Configuration roleNamesOnlyConfig = new Configuration();
				rolesThatReferenceThemselves.forEach(role -> roleNamesOnlyConfig.addRole(role));
				logger.info("Submitting CMA configuration containing roles that reference themselves and do not yet exist");
				new Configurations(roleNamesOnlyConfig).submit(context.getManageClient());
			}
		}
	}

	/**
	 * If the resource was saved during the first pass - i.e. when roles and permissions have been removed from the role
	 * - then it must be processed during the second pass so that those roles/permissions can be added back. Thus, the
	 * incremental check on the file must be ignored. Note that this only matters during an incremental deploy, which
	 * means that the ResourceReference should have a single File in it.
	 */
	@Override
	protected void afterResourceSaved(ResourceManager mgr, CommandContext context, ResourceReference resourceReference, SaveReceipt receipt) {
		if (removeRolesAndPermissionsDuringDeployment && resourceReference != null) {
			ignoreIncrementalCheckForFile(resourceReference.getLastFile());
		}
		super.afterResourceSaved(mgr, context, resourceReference, receipt);
	}

	/**
	 * If this is the first time roles are being deployed by this command - indicated by the removeRolesAndPermissionsDuringDeployment
	 * class variable - then each payload is modified so that default permissions and role references are not included,
	 * thus ensuring that the role can be created successfully.
	 * <p>
	 * If this is the second time that roles are being deployed by this command, then the entire payload is sent. However,
	 * if the role doesn't have any default permissions or role references, it will not be deployed a second time, as
	 * there was nothing missing from the first deployment of the role.
	 *
	 * @param mgr
	 * @param context
	 * @param f
	 * @param payload
	 * @return
	 */
	@Override
	protected String adjustPayloadBeforeSavingResource(CommandContext context, File f, String payload) {
		payload = super.adjustPayloadBeforeSavingResource(context, f, payload);

		if (resourceMapper == null) {
			API api = new API(context.getManageClient(), context.getAdminManager());
			resourceMapper = new DefaultResourceMapper(api);
		}

		Role role = resourceMapper.readResource(payload, Role.class);

		// Is this the first time the roles are being deployed?
		if (removeRolesAndPermissionsDuringDeployment) {
			if (role.hasPermissionsOrRoles()) {
				role.clearPermissionsAndRoles();
				return role.getJson();
			} else {
				roleNamesThatDontNeedToBeRedeployed.add(role.getRoleName());
				return payload;
			}
		}
		// Else it's the second time roles are being deployed, but no need to deploy a role if it doesn't have any default permissions or role references
		else if (roleNamesThatDontNeedToBeRedeployed.contains(role.getRoleName())) {
			if (logger.isInfoEnabled()) {
				logger.info("Not redeploying role " + role.getRoleName() + ", as it does not have any default permissions or references to other roles");
			}
			return null;
		}
		// Else log a message to indicate that the role is being redeployed
		else if (logger.isInfoEnabled()) {
			logger.info("Redeploying role " + role.getRoleName() + " with default permissions and references to other roles included");
		}

		return payload;
	}

	protected File[] getResourceDirs(CommandContext context) {
		return findResourceDirs(context, configDir -> configDir.getRolesDir());
	}

	@Override
	protected ResourceManager getResourceManager(CommandContext context) {
		return new RoleManager(context.getManageClient());
	}

	public void setDeployRolesInTwoPhases(boolean deployRolesInTwoPhases) {
		this.deployRolesInTwoPhases = deployRolesInTwoPhases;
	}
}

