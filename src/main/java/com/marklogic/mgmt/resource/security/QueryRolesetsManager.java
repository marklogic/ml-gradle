package com.marklogic.mgmt.resource.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.PayloadParser;
import com.marklogic.mgmt.resource.AbstractResourceManager;
import com.marklogic.rest.util.Fragment;

import java.util.List;

public class QueryRolesetsManager extends AbstractResourceManager {

	private boolean updateAllowed = false;

	public QueryRolesetsManager(ManageClient client) {
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
	public String getPropertiesPath(String resourceNameOrId, String... resourceUrlParams) {
		String id = getIdForRoleNames(resourceNameOrId);
		if (id == null) {
			throw new RuntimeException("Could not find a query-roleset with roles: " + resourceNameOrId);
		} else return getResourcesPath()  + "/" + id + "/properties";
	}

	@Override
	public String getResourcePath(String resourceNameOrId, String... resourceUrlParams) {
		String id = getIdForRoleNames(resourceNameOrId);
		if (id == null) {
			throw new RuntimeException("Could not find a query-roleset with roles: " + resourceNameOrId);
		}else return getResourcesPath()  + "/" + id;
	}

	@Override
	public boolean exists(String resourceNameOrId, String... resourceUrlParams) {
		if (logger.isInfoEnabled()) {
			logger.info("Checking for existence of resource: " + resourceNameOrId);
		}
		Fragment f = getAsXml();
		return f.elementExists(format(
			"/node()/*[local-name(.) = 'list-items']/node()[*[local-name(.) = 'idref'] = '%s']",
			getIdForRoleNames(resourceNameOrId)));
	}

	public String getIdForRoleNames(String roles) {
		Fragment f = getAsXml();
		String xpath = "/node()/*[local-name(.) = 'list-items']/node()/*[local-name(.) = 'idref']";
		String roleSetId = null;

		//Transform roles into role JSON array
		JsonNode roleArray = payloadParser.parseJson(roles);

		//Get list of existing rolesets
		for(String id : f.getElementValues(xpath)) {
			String response =
				payloadParser.getPayloadFieldValue(
                    getManageClient().getJsonAsSecurityUser(getResourcesPath() + "/" + id + "/properties"),
                    getIdFieldName()
				);

			//does this roleset contain the same list of roles?
			if (roleArray.equals(payloadParser.parseJson(response))) {
				roleSetId = id;
				break;
			}
		}
		return roleSetId;
	}

}
