/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.command.security;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.appdeployer.command.*;
import com.marklogic.mgmt.PayloadParser;
import com.marklogic.mgmt.api.configuration.Configuration;
import com.marklogic.mgmt.api.security.Privilege;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.security.PrivilegeManager;
import org.apache.commons.lang3.builder.EqualsBuilder;

import java.io.File;
import java.util.function.BiPredicate;

public class DeployPrivilegesCommand extends AbstractResourceCommand implements SupportsCmaCommand {

	private boolean removeRolesBeforeSaving = true;

	public DeployPrivilegesCommand() {
		setExecuteSortOrder(SortOrderConstants.DEPLOY_PRIVILEGES);
		setUndoSortOrder(SortOrderConstants.DELETE_PRIVILEGES);

		setSupportsResourceMerging(true);
		setResourceClassType(Privilege.class);
	}

	@Override
	protected File[] getResourceDirs(CommandContext context) {
		return findResourceDirs(context, configDir -> configDir.getPrivilegesDir());
	}

	@Override
	protected ResourceManager getResourceManager(CommandContext context) {
		return new PrivilegeManager(context.getManageClient());
	}

	@Override
	protected String adjustPayloadBeforeSavingResource(CommandContext context, File f, String payload) {
		payload = super.adjustPayloadBeforeSavingResource(context, f, payload);
		return removeRolesBeforeSaving ? new PayloadParser().excludeProperties(payload, "role") : payload;
	}

	@Override
	public boolean cmaShouldBeUsed(CommandContext context) {
		return context.getAppConfig().getCmaConfig().isDeployPrivileges();
	}

	@Override
	public void addResourceToConfiguration(ObjectNode resource, Configuration configuration) {
		if (removeRolesBeforeSaving && resource != null && resource.has("role")) {
			resource.remove("role");
		}

		configuration.addPrivilege(resource);
	}

	@Override
	protected void deployConfiguration(CommandContext context, Configuration config) {
		if (context.getAppConfig().getCmaConfig().isCombineRequests()) {
			logger.info("Adding privileges to combined CMA request");
			context.addCmaConfigurationToCombinedRequest(config);
		} else {
			super.deployConfiguration(context, config);
		}
	}

	@Override
	protected BiPredicate<ResourceReference, ResourceReference> getBiPredicateForMergingResources() {
		return new PrivilegeBiPredicate();
	}

	public boolean isRemoveRolesBeforeSaving() {
		return removeRolesBeforeSaving;
	}

	public void setRemoveRolesBeforeSaving(boolean removeRolesBeforeSaving) {
		this.removeRolesBeforeSaving = removeRolesBeforeSaving;
	}
}

class PrivilegeBiPredicate implements BiPredicate<ResourceReference, ResourceReference> {
	@Override
	public boolean test(ResourceReference reference1, ResourceReference reference2) {
		EqualsBuilder b = new EqualsBuilder();

		final ObjectNode node1 = reference1.getObjectNode();
		final ObjectNode node2 = reference2.getObjectNode();

		b.append(node1.get("privilege-name").asText(), node2.get("privilege-name").asText());
		b.append(node1.has("kind") ? node1.get("kind").asText() : null, node2.has("kind") ? node2.get("kind").asText() : null);

		return b.isEquals();
	}
}
