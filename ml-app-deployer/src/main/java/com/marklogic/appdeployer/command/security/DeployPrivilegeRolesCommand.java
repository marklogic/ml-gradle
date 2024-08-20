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

import com.marklogic.appdeployer.command.AbstractResourceCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.ResourceReference;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.api.API;
import com.marklogic.mgmt.api.security.Privilege;
import com.marklogic.mgmt.mapper.DefaultResourceMapper;
import com.marklogic.mgmt.mapper.ResourceMapper;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.security.PrivilegeManager;

import java.io.File;
import java.util.function.BiPredicate;

/**
 * Intended to run after roles and privileges have been deployed so that any roles associated with privileges can be
 * safely deployed.
 */
public class DeployPrivilegeRolesCommand extends AbstractResourceCommand {

	private ResourceMapper resourceMapper;

	public DeployPrivilegeRolesCommand() {
		setExecuteSortOrder(SortOrderConstants.DEPLOY_PRIVILEGE_ROLES);
		setUndoSortOrder(SortOrderConstants.DELETE_PRIVILEGES);

		setSupportsResourceMerging(true);
		setResourceClassType(Privilege.class);
	}

	@Override
	public void undo(CommandContext context) {
		logger.info("Nothing to do, as DeployPrivilegesCommand is expected to delete privileges");
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
		if (payload != null) {
			if (resourceMapper == null) {
				resourceMapper = new DefaultResourceMapper(new API(context.getManageClient()));
			}
			Privilege p = resourceMapper.readResource(payload, Privilege.class);
			if (p.getRole() == null || p.getRole().isEmpty()) {
				return null;
			}
		}
		return payload;
	}

	@Override
	protected BiPredicate<ResourceReference, ResourceReference> getBiPredicateForMergingResources() {
		return new PrivilegeBiPredicate();
	}
}
