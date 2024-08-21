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
package com.marklogic.mgmt.resource.groups;

import com.marklogic.mgmt.DeleteReceipt;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.resource.AbstractResourceManager;
import com.marklogic.rest.util.Fragment;
import org.jdom2.Element;
import org.jdom2.Namespace;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GroupManager extends AbstractResourceManager {

	public GroupManager(ManageClient manageClient) {
		super(manageClient);
	}

	@Override
	protected boolean useSecurityUser() {
		return true;
	}

	@Override
	public DeleteReceipt delete(String payload, String... resourceUrlParams) {
		String resourceId = getResourceId(payload);
		if (resourceId != null && resourceId.equalsIgnoreCase("DEFAULT")) {
			return new DeleteReceipt(resourceId, null, false);
		}
		return super.delete(payload);
	}

	public List<String> getHostNames(String groupName) {
		String path = super.getResourcePath(groupName);
		Fragment xml = getManageClient().getXml(path);

		List<String> hostNames = new ArrayList<>();
		Namespace ns = Namespace.getNamespace("http://marklogic.com/manage/groups");
		for (Element el : xml.getElements("/g:group-default/g:relations/g:relation-group[g:typeref = 'hosts']/g:relation")) {
			String hostName = el.getChildText("nameref", ns);
			hostNames.add(hostName);
		}

		return hostNames;
	}

	public Map<String, String> getHostIdsAndNames(String groupName) {
		String path = super.getResourcePath(groupName);
		Fragment xml = getManageClient().getXml(path);

		Map<String, String> map = new LinkedHashMap<>();
		Namespace ns = Namespace.getNamespace("http://marklogic.com/manage/groups");
		for (Element el : xml.getElements("/g:group-default/g:relations/g:relation-group[g:typeref = 'hosts']/g:relation")) {
			String hostId = el.getChildText("idref", ns);
			String hostName = el.getChildText("nameref", ns);
			map.put(hostId, hostName);
		}

		return map;
	}
}
