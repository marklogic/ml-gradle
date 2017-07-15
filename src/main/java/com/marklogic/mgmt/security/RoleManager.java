package com.marklogic.mgmt.security;

import com.marklogic.mgmt.AbstractResourceManager;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.SaveReceipt;
import com.marklogic.mgmt.api.API;
import com.marklogic.mgmt.api.security.Role;

public class RoleManager extends AbstractResourceManager {

	private API api;

	public RoleManager(ManageClient client) {
		super(client);
	}

	@Override
	protected boolean useAdminUser() {
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
		if (api == null) {
			api = new API(getManageClient());
		}

		/**
		 * TODO In version 3.x, this will be handled via a separate class.
		 */
		Role role = null;
		if (payloadParser.isJsonPayload(payload)) {
			role = Role.parseJson(api, payload);
		} else {
			role = Role.parseXml(payload);
			role.setApi(api);
			role.setObjectMapper(api.getObjectMapper());
		}

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
