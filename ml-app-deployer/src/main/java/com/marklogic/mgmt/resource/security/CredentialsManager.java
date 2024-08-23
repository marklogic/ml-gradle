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
