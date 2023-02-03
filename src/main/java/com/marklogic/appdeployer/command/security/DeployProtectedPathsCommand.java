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
import com.marklogic.mgmt.api.configuration.Configuration;
import com.marklogic.mgmt.api.security.protectedpath.ProtectedPath;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.security.ProtectedPathManager;

import java.io.File;

public class DeployProtectedPathsCommand extends AbstractResourceCommand implements SupportsCmaCommand {

	public DeployProtectedPathsCommand() {
		setExecuteSortOrder(SortOrderConstants.DEPLOY_PROTECTED_PATHS);
		setUndoSortOrder(SortOrderConstants.DELETE_PROTECTED_PATHS);

		setResourceClassType(ProtectedPath.class);
	}

	@Override
	public boolean cmaShouldBeUsed(CommandContext context) {
		return context.getAppConfig().getCmaConfig().isDeployProtectedPaths();
	}

	@Override
	public void addResourceToConfiguration(ObjectNode resource, Configuration configuration) {
		configuration.addProtectedPath(resource);
	}

	@Override
	protected void deployConfiguration(CommandContext context, Configuration config) {
		if (context.getAppConfig().getCmaConfig().isCombineRequests()) {
			logger.info("Adding protected paths to combined CMA request");
			context.addCmaConfigurationToCombinedRequest(config);
		} else {
			super.deployConfiguration(context, config);
		}
	}

	@Override
	protected File[] getResourceDirs(CommandContext context) {
		return findResourceDirs(context, configDir -> configDir.getProtectedPathsDir());
	}

	@Override
	protected ResourceManager getResourceManager(CommandContext context) {
		return new ProtectedPathManager(context.getManageClient());
	}
}
