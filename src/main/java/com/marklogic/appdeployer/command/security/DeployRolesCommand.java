package com.marklogic.appdeployer.command.security;

import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.appdeployer.command.AbstractResourceCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.appdeployer.command.SupportsCmaCommand;
import com.marklogic.mgmt.SaveReceipt;
import com.marklogic.mgmt.api.configuration.Configuration;
import com.marklogic.mgmt.api.configuration.Configurations;
import com.marklogic.mgmt.api.security.Role;
import com.marklogic.mgmt.api.security.RoleObjectNodesSorter;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.security.RoleManager;
import com.marklogic.mgmt.util.ObjectMapperFactory;
import com.marklogic.mgmt.util.ObjectNodesSorter;
import com.marklogic.rest.util.ResourcesFragment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * As of 3.15.0, this no longer deploys roles in two phases. This is due to the new sorting class, which uses a
 * topological sort to properly account for role dependencies.
 * <p>
 * However, to take advantage of this sorting, this class instructs the parent class to always construct a CMA request
 * for all of the roles that are found. This allows for all of the roles to be sorted easily, as they're in a list of
 * ObjectNode objects. Once the CMA request is ready to be submitted, this class then checks to see if CMA should
 * actually be used. If not, then each role is submitted individually in the correct order.
 */
public class DeployRolesCommand extends AbstractResourceCommand implements SupportsCmaCommand {

	private ObjectNodesSorter objectNodesSorter = new RoleObjectNodesSorter();

	public DeployRolesCommand() {
		setExecuteSortOrder(SortOrderConstants.DEPLOY_ROLES);
		setUndoSortOrder(SortOrderConstants.DELETE_ROLES);

		setSupportsResourceMerging(true);
		setResourceIdPropertyName("role-name");
		setResourceClassType(Role.class);
	}

	/**
	 * This tells the parent class to always build a Configuration, even if CMA isn't available. When it's time to
	 * deploy the configuration, we'll check to see if CMA truly is available.
	 *
	 * @param context
	 * @return
	 */
	@Override
	protected boolean useCmaForDeployingResources(CommandContext context) {
		return true;
	}

	@Override
	public boolean cmaShouldBeUsed(CommandContext context) {
		return true;
	}

	@Override
	public void addResourceToConfiguration(ObjectNode resource, Configuration configuration) {
		configuration.addRole(resource);
	}

	/**
	 * Before a role configuration can be submitted, the roles within the configuration must be sorted based on their
	 * dependencies.
	 * <p>
	 * Then, if CMA is available and the user wants it to be used, the configuration is either submitted or added to a
	 * combined CMA request. Otherwise, each role will be created individually. In both cases, a check is made on
	 * each role to see if it does not exist yet and refers to itself. If so, such roles will be created first without
	 * any dependencies.
	 *
	 * @param context
	 * @param config
	 */
	@Override
	protected void deployConfiguration(CommandContext context, Configuration config) {
		List<ObjectNode> roleNodes = config.getRoles();
		if (roleNodes == null || roleNodes.isEmpty()) {
			return;
		}

		if (objectNodesSorter != null) {
			logger.info("Sorting roles before they are saved");
			roleNodes = objectNodesSorter.sortObjectNodes(roleNodes);
			config.setRoles(roleNodes);
		}

		if (context.getAppConfig().getCmaConfig().isDeployRoles() && cmaEndpointExists(context)) {
			submitRolesConfigurationViaCma(context, config);
		} else {
			submitRolesIndividually(context, roleNodes);
		}
	}

	protected void submitRolesConfigurationViaCma(CommandContext context, Configuration config) {
		submitConfigurationWithRolesThatReferenceThemselves(context, config.getRoles());
		if (context.getAppConfig().getCmaConfig().isCombineRequests()) {
			logger.info("Adding roles to combined CMA request");
			context.addCmaConfigurationToCombinedRequest(config);
		} else {
			super.deployConfiguration(context, config);
		}
	}

	protected void submitRolesIndividually(CommandContext context, List<ObjectNode> roleNodes) {
		RoleManager roleManager = new RoleManager(context.getManageClient());

		findRolesThatReferenceThemselves(context, roleNodes).forEach(role -> {
			roleManager.save(format("{\"role-name\":\"%s\"}", role.getRoleName()));
		});

		roleNodes.forEach(roleNode -> {
			SaveReceipt receipt = saveResource(roleManager, context, roleNode.toString());
			afterResourceSaved(roleManager, context, null, receipt);
		});
	}

	/**
	 * If a role refers to itself via permissions, that role won't be created by CMA. Instead, a separate CMA request
	 * is constructed, with each such role only having a role-name, and then immediately submitted so that the roles
	 * are guaranteed to exist. Note that only roles that don't exist yet will be included in this request (if they
	 * already exist, then no problem will occur).
	 *
	 * @param context
	 * @param roles
	 */
	protected void submitConfigurationWithRolesThatReferenceThemselves(CommandContext context, List<ObjectNode> roles) {
		List<Role> rolesThatReferenceThemselves = findRolesThatReferenceThemselves(context, roles);

		if (!rolesThatReferenceThemselves.isEmpty()) {
			Configuration roleNamesOnlyConfig = new Configuration();
			rolesThatReferenceThemselves.forEach(role -> {
				ObjectNode node = ObjectMapperFactory.getObjectMapper().createObjectNode();
				node.put("role-name", role.getRoleName());
				roleNamesOnlyConfig.addRole(node);
			});
			logger.info("Submitting CMA configuration containing roles that reference themselves and do not yet exist");
			new Configurations(roleNamesOnlyConfig).submit(context.getManageClient());
		}
	}

	/**
	 * Returns a list of roles, one for each role in the given ObjectNode list that does not exist yet and refers to
	 * itself.
	 *
	 * @param context
	 * @param roles
	 * @return
	 */
	protected List<Role> findRolesThatReferenceThemselves(CommandContext context, List<ObjectNode> roles) {
		ObjectReader reader = ObjectMapperFactory.getObjectMapper().readerFor(Role.class);
		List<Role> rolesThatReferenceThemselves = new ArrayList<>();
		ResourcesFragment rolesXml = new RoleManager(context.getManageClient()).getAsXml();
		roles.forEach(role -> {
			try {
				Role r = reader.readValue(role);
				if (r.hasPermissionWithOwnRoleName() && !rolesXml.resourceExists(r.getRoleName())) {
					rolesThatReferenceThemselves.add(r);
				}
			} catch (IOException e) {
				throw new RuntimeException("Unable to read ObjectNode into Role; node: " + role, e);
			}
		});
		return rolesThatReferenceThemselves;
	}

	protected File[] getResourceDirs(CommandContext context) {
		return findResourceDirs(context, configDir -> configDir.getRolesDir());
	}

	@Override
	protected ResourceManager getResourceManager(CommandContext context) {
		return new RoleManager(context.getManageClient());
	}

	public void setObjectNodesSorter(ObjectNodesSorter objectNodesSorter) {
		this.objectNodesSorter = objectNodesSorter;
	}
}

