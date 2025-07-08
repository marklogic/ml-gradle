/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt.resource.security;

import com.marklogic.mgmt.DeleteReceipt;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.resource.AbstractResourceManager;

public class CredentialsManager extends AbstractResourceManager {

	public CredentialsManager(ManageClient client) {
		super(client, true);
	}

	@Override
	public String getResourcesPath() {
		return "/manage/v2/credentials/properties";
	}


	@Override
	protected String getIdFieldName() {
		return "type";
	}

	@Override
	protected String getResourceId(String payload) {
		return getCredentialsType(payload);
	}

	@Override
	public DeleteReceipt delete(String payload, String... resourceUrlParams) {
		final String type = getCredentialsType(payload);
		final String path = "/manage/v2/credentials/properties?type=" + type;
		// The DELETE endpoint - https://docs.marklogic.com/REST/DELETE/manage/v2/credentials/properties - seems to
		// erroneously require a Content-type header, even though there's no request body.
		super.deleteAtPath(path, "Content-type", "application/json");
		return new DeleteReceipt(type, path, true);
	}

	private String getCredentialsType(String payload) {
		if (payloadParser.isJsonPayload(payload)) {
			return payloadParser.getPayloadFieldValue(payload, getIdFieldName());
		}
		return payloadParser.getPayloadFieldValue(payload, "azure", false) != null ? "azure" : "aws";
	}
}
