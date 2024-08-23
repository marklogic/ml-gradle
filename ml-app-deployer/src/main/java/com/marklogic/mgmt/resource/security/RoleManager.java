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
package com.marklogic.mgmt.resource.security;

import com.marklogic.mgmt.resource.AbstractResourceManager;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.SaveReceipt;
import com.marklogic.mgmt.api.API;
import com.marklogic.mgmt.api.security.Role;
import com.marklogic.mgmt.mapper.DefaultResourceMapper;
import com.marklogic.mgmt.mapper.ResourceMapper;

public class RoleManager extends AbstractResourceManager {

	private ResourceMapper resourceMapper;

	public RoleManager(ManageClient client) {
		super(client);
	}

	@Override
	protected boolean useSecurityUser() {
		return true;
	}

	/**
	 * When a new role is created, we need to check to see if it has permissions that reference the role name. If so,
	 * we can't create the role with the given payload - the Manage API will throw an error. Instead, we create the
	 * role minus the permissions, and then we perform an update to the role with the given payload, which
	 * includes the permissions.
	 *
	 * @param payload
	 * @param resourceId
	 * @return
	 */
	@Override
	protected SaveReceipt createNewResource(String payload, String resourceId) {
		if (resourceMapper == null) {
			API api = new API(getManageClient());
			resourceMapper = new DefaultResourceMapper(api);
		}

		Role role = resourceMapper.readResource(payload, Role.class);

		if (role.hasPermissionWithOwnRoleName()) {
			role.getPermission().clear();
			if (logger.isInfoEnabled()) {
				logger.info("Creating role '" + resourceId + "' that has permissions that refer to itself, " +
					"so first creating role without permissions, and then updating role with permissions");
			}
			SaveReceipt receipt = super.createNewResource(role.getJson(), resourceId);
			super.updateResource(payload, resourceId);
			return receipt;
		} else {
			return super.createNewResource(payload, resourceId);
		}
	}
}
