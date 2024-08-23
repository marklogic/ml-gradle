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

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.SaveReceipt;
import com.marklogic.mgmt.resource.AbstractResourceManager;
import com.marklogic.rest.util.Fragment;
import com.marklogic.rest.util.ResourcesFragment;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

public class AmpManager extends AbstractResourceManager {

	private String namespace;
	private String documentUri;
	private String modulesDatabase;

	public AmpManager(ManageClient client) {
		super(client);
	}

	@Override
	protected boolean useSecurityUser() {
		return true;
	}

	public String getResourcePath(String resourceNameOrId) {
		return super.getResourcePath(resourceNameOrId, "namespace", namespace, "document-uri", documentUri,
			"modules-database", modulesDatabase);
	}

	public String getPropertiesPath(String resourceNameOrId) {
		return super.getPropertiesPath(resourceNameOrId, "namespace", namespace, "document-uri", documentUri,
			"modules-database", modulesDatabase);
	}

	@Override
	protected String getIdFieldName() {
		return "local-name";
	}

	/**
	 * We have to override how this works in the parent class because the parent class assumes that existence
	 * can be based solely on the resource ID. But for an amp, we need to use the resource ID (local-name),
	 * document-uri, namespace, and modules-database.
	 */
	@Override
	public SaveReceipt save(String payload) {
		String resourceId = getResourceId(payload);
		String label = getResourceName();
		String path = null;
		ResponseEntity<String> response = null;
		if (ampExists(payload)) {
			return updateResource(payload, resourceId);
		} else {
			logger.info(format("Creating %s: %s", label, resourceId));
			path = getCreateResourcePath(payload);
			response = postPayload(getManageClient(), path, payload);
			logger.info(format("Created %s: %s", label, resourceId));
		}
		return new SaveReceipt(resourceId, payload, path, response);
	}

	/**
	 * Uses local-name, document-uri, modules-database, and namespace to determine if the amp exists. Since we
	 * do this comparison against the XML returned by /manage/v2/amps, if no modules-database is set, we have
	 * to substitute in the value "filesystem", as that's what is returned by that endpoint.
	 *
	 * @param payload
	 * @return
	 */
	public boolean ampExists(String payload) {
		String resourceId = getResourceId(payload);
		AmpParams params = getAmpParams(payload);
		return ampExists(getAsXml(), resourceId, params.documentUri, params.namespace, params.modulesDatabase);
	}

	public boolean ampExists(ResourcesFragment resources, String localName, String documentUri, String namespace, String modulesDatabase) {
		String xpath = "/node()/*[local-name(.) = 'list-items']/node()[" +
			"(*[local-name(.) = 'nameref'] = '%s' or *[local-name(.) = 'idref'] = '%s')" +
			" and *[local-name(.) = 'document-uri'] = '%s'";
		xpath = format(xpath, localName, localName, documentUri);
		if (namespace != null) {
			xpath += format(" and *[local-name(.) = 'namespace'] = '%s'", namespace);
		}
		if (modulesDatabase != null) {
			xpath += format(" and *[local-name(.) = 'modules-database'] = '%s'", modulesDatabase);
		} else {
			xpath += format(" and *[local-name(.) = 'modules-database'] = 'filesystem'");
		}
		xpath += "]";
		return resources.elementExists(xpath);
	}

	@Override
	protected String[] getUpdateResourceParams(String payload) {
		List<String> params = new ArrayList<>();
		AmpParams ampParams = getAmpParams(payload);
		params.add("document-uri");
		params.add(ampParams.documentUri);
		// Building params for a DELETE requires namespace= in case namespace does not exist, but testing shows that
		// this is not required for an update, even though the docs for a PUT on an amp indicate that namespace is
		// required. The ManageAmpsTest suggests otherwise.
		if (ampParams.namespace != null) {
			params.add("namespace");
			params.add(ampParams.namespace);
		}
		if (ampParams.modulesDatabase != null) {
			params.add("modules-database");
			params.add(ampParams.modulesDatabase);
		}
		return params.toArray(new String[]{});
	}

	/**
	 * Note that the parent class's delete method does an existence check based on just the resource ID - in an
	 * amp's case, the local name. That will still work, because when the parent class builds the properties path
	 * for the amp resource, it will call this method and include the other parameters needed to uniquely refer
	 * to the amp. Worst case - a call is made to delete an amp that no longer exists, but another amp exists with
	 * the same local name. The parent class will think the amp exists, but when it tries the delete call, it
	 * won't find the amp with all the params, and the call will succeed without deleting anything.
	 */
	@Override
	protected String[] getDeleteResourceParams(String payload) {
		List<String> params = new ArrayList<>();
		AmpParams ampParams = getAmpParams(payload);
		params.add("document-uri");
		params.add(ampParams.documentUri);
		// The DELETE endpoint requires 'namespace=' to be passed in case no namespace is set for an amp, which will
		// always be the case for an amp on a JS function
		params.add("namespace");
		if (ampParams.namespace != null) {
			params.add(ampParams.namespace);
		} else {
			params.add("");
		}
		if (ampParams.modulesDatabase != null) {
			params.add("modules-database");
			params.add(ampParams.modulesDatabase);
		}
		return params.toArray(new String[]{});
	}

	/**
	 * @param payload
	 * @return an AmpParams object containing the values of the 3 amp properties - besides local name - that are
	 * needed to uniquely refer to an amp.
	 */
	public AmpParams getAmpParams(String payload) {
		AmpParams params = new AmpParams();
		if (payloadParser.isJsonPayload(payload)) {
			JsonNode node = payloadParser.parseJson(payload);
			params.documentUri = node.get("document-uri").asText();
			if (node.has("namespace")) {
				params.namespace = node.get("namespace").asText();
			}
			if (node.has("modules-database")) {
				params.modulesDatabase = node.get("modules-database").asText();
			}
		} else {
			Fragment f = new Fragment(payload);
			params.documentUri = f.getElementValue("/node()/*[local-name(.) = 'document-uri']");
			String val = f.getElementValue("/node()/*[local-name(.) = 'namespace']");
			if (val != null) {
				params.namespace = val;
			}
			val = f.getElementValue("/node()/*[local-name(.) = 'modules-database']");
			if (val != null) {
				params.modulesDatabase = val;
			}
		}
		return params;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public void setDocumentUri(String documentUri) {
		this.documentUri = documentUri;
	}

	public void setModulesDatabase(String modulesDatabase) {
		this.modulesDatabase = modulesDatabase;
	}
}

class AmpParams {
	public String documentUri;
	public String modulesDatabase;
	public String namespace;
}
