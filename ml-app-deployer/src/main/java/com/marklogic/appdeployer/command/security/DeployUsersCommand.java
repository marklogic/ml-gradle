/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.appdeployer.command.security;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.appdeployer.command.AbstractResourceCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.appdeployer.command.SupportsCmaCommand;
import com.marklogic.mgmt.PayloadParser;
import com.marklogic.mgmt.api.configuration.Configuration;
import com.marklogic.mgmt.api.security.User;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.security.UserManager;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class DeployUsersCommand extends AbstractResourceCommand implements SupportsCmaCommand {

	// Defines users that, by default, this command will never undeploy
	private Set<String> defaultUsersToNotUndeploy = new HashSet<>();

	public DeployUsersCommand() {
		setExecuteSortOrder(SortOrderConstants.DEPLOY_USERS);
		setUndoSortOrder(SortOrderConstants.DELETE_USERS);

		setSupportsResourceMerging(true);
		setResourceClassType(User.class);
		setResourceIdPropertyName("user-name");

		initializeDefaultUsersToNotUndeploy();
	}

	protected void initializeDefaultUsersToNotUndeploy() {
		defaultUsersToNotUndeploy = new HashSet<>();
		defaultUsersToNotUndeploy.add("admin");
		defaultUsersToNotUndeploy.add("healthcheck");
		defaultUsersToNotUndeploy.add("infostudio-admin");
		defaultUsersToNotUndeploy.add("nobody");
	}

	@Override
	protected String adjustPayloadBeforeDeletingResource(ResourceManager mgr, CommandContext context, File f, String payload) {
		if (defaultUsersToNotUndeploy != null && !defaultUsersToNotUndeploy.isEmpty()) {
			final String username = new PayloadParser().getPayloadFieldValue(payload, "user-name", false);
			if (username != null && defaultUsersToNotUndeploy.contains(username)) {
				logger.info(format("Not undeploying user '%s', as it's included in the list of users to not undeploy", username));
				return null;
			}
		}

		return super.adjustPayloadBeforeDeletingResource(mgr, context, f, payload);
	}

	protected File[] getResourceDirs(CommandContext context) {
		return findResourceDirs(context.getAppConfig(), configDir -> configDir.getUsersDir());
	}

	@Override
	protected ResourceManager getResourceManager(CommandContext context) {
		return new UserManager(context.getManageClient());
	}

	@Override
	public boolean cmaShouldBeUsed(CommandContext context) {
		return context.getAppConfig().getCmaConfig().isDeployUsers();
	}

	@Override
	public void addResourceToConfiguration(ObjectNode resource, Configuration configuration) {
		configuration.addUser(resource);
	}

	@Override
	protected void deployConfiguration(CommandContext context, Configuration config) {
		if (context.getAppConfig().getCmaConfig().isCombineRequests()) {
			logger.info("Adding users to combined CMA request");
			context.addCmaConfigurationToCombinedRequest(config);
		} else {
			super.deployConfiguration(context, config);
		}
	}

	public Set<String> getDefaultUsersToNotUndeploy() {
		return defaultUsersToNotUndeploy;
	}

	public void setDefaultUsersToNotUndeploy(Set<String> defaultUsersToNotUndeploy) {
		this.defaultUsersToNotUndeploy = defaultUsersToNotUndeploy;
	}
}
