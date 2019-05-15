package com.marklogic.mgmt.resource.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.resource.AbstractResourceManager;
import com.marklogic.rest.util.ResourcesFragment;
import org.apache.commons.lang3.StringUtils;

public class QueryRolesetManager extends AbstractResourceManager {

	public QueryRolesetManager(ManageClient client) {
		super(client);
	}

	@Override
	protected boolean useSecurityUser() {
		return true;
	}

	@Override
	public String getResourcesPath() {
		return "/manage/v2/query-rolesets";
	}

	@Override
	protected String getResourceName() {
		return "query-rolesets";
	}

	@Override
	protected String getIdFieldName() {
		return "role-name";
	}

	@Override
	public String getResourcePath(String resourceNameOrId, String... resourceUrlParams) {
		if (isRoleId(resourceNameOrId)) {
			return super.getResourcePath(resourceNameOrId, resourceUrlParams);
		}
		
		String id = getIdForRoleNames(resourceNameOrId, getAsXml());
		if (id == null) {
			throw new RuntimeException("Could not find a query-roleset with roles: " + resourceNameOrId);
		}
		return getResourcesPath() + "/" + id;
	}

	@Override
	public boolean exists(String resourceNameOrId, String... resourceUrlParams) {
		if (logger.isInfoEnabled()) {
			logger.info("Checking for existence of resource: " + resourceNameOrId);
		}
		ResourcesFragment resourcesFragment = getAsXml();
		return resourcesFragment.elementExists(format(
			"/node()/*[local-name(.) = 'list-items']/node()[*[local-name(.) = 'idref'] = '%s']",
			getIdForRoleNames(resourceNameOrId, resourcesFragment)));
	}

	/**
	 * Query rolesets are tricky because as of ML 9.0-9, a role ID must be sent to the Manage API. But in order to get
	 * that role ID, we have to look at every existing roleset and see if it has the same array of roles as the incoming
	 * JSON array string of roles. Not efficient, but no other way to do it.
	 *
	 * @param jsonRolesArray
	 * @return
	 */
	public String getIdForRoleNames(String jsonRolesArray, ResourcesFragment resourcesXml) {
		JsonNode roleArray = payloadParser.parseJson(jsonRolesArray);
		for (String roleId : resourcesXml.getListItemIdRefs()) {
			String myRoles = payloadParser.getPayloadFieldValue(getPropertiesAsJson(roleId), getIdFieldName());
			if (roleArray.equals(payloadParser.parseJson(myRoles))) {
				return roleId;
			}
		}
		return null;
	}

	private boolean isRoleId(String resourceNameOrId) {
		return StringUtils.isNumeric(resourceNameOrId);
	}
}
