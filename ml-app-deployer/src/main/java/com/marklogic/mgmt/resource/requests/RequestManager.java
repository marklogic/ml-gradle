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
package com.marklogic.mgmt.resource.requests;

import com.marklogic.mgmt.AbstractManager;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.rest.util.Fragment;
import org.jdom2.Element;

import java.util.List;

public class RequestManager extends AbstractManager {

	private ManageClient client;

	public RequestManager(ManageClient client) {
		this.client = client;
	}

	public Fragment getRequests() {
		return client.getXml("/manage/v2/requests");
	}

	public int getRequestCountForRelationId(String id) {
		return getRequestsForRelationId(id).size();
	}

	public List<Element> getRequestsForRelationId(String id) {
		return getRequests().getElements(format("//req:list-items/req:list-item[req:relation-id = '%s']", id));
	}
}
