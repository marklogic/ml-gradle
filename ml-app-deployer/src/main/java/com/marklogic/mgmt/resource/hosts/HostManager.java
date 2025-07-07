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
package com.marklogic.mgmt.resource.hosts;

import com.marklogic.mgmt.AbstractManager;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.rest.util.Fragment;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.springframework.http.ResponseEntity;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HostManager extends AbstractManager {

	private ManageClient manageClient;

	public HostManager(ManageClient manageClient) {
		this.manageClient = manageClient;
	}

	public List<String> getHostIds() {
		return getHosts().getElementValues("/h:host-default-list/h:list-items/h:list-item/h:idref");
	}

	public List<String> getHostNames() {
		return getHosts().getElementValues("/h:host-default-list/h:list-items/h:list-item/h:nameref");
	}

	/**
	 * @return a map of host names to optional zones. For performance reasons, as soon as a host is found to not have
	 * a zone, then an empty map will be returned. Currently, the only use case for this map is to create forest replicas
	 * across zones. But if any host does not have a zone, then zones do not matter.
	 * @since 6.0.0
	 */
	public Map<String, String> getHostNamesAndZones() {
		Map<String, String> map = new LinkedHashMap<>();
		for (String hostName : getHostNames()) {
			String json = manageClient.getJson("/manage/v2/hosts/%s/properties".formatted(hostName));
			String zone = payloadParser.getPayloadFieldValue(json, "zone");
			if (zone == null || zone.trim().isEmpty()) {
				return map;
			}
			map.put(hostName, zone);
		}
		return map;
	}

	/**
	 * @return a map with an entry for each host, with the key of the host being the host ID and the value being the
	 * host name
	 */
	public Map<String, String> getHostIdsAndNames() {
		Fragment xml = getHosts();
		Map<String, String> map = new LinkedHashMap<>();
		Namespace ns = Namespace.getNamespace("http://marklogic.com/manage/hosts");
		for (Element el : xml.getElements("/h:host-default-list/h:list-items/h:list-item")) {
			String hostId = el.getChildText("idref", ns);
			String hostName = el.getChildText("nameref", ns);
			map.put(hostId, hostName);
		}
		return map;
	}

	public Fragment getHosts() {
		return manageClient.getXml("/manage/v2/hosts");
	}

	public ResponseEntity<String> setHostToGroup(String hostIdOrName, String groupIdOrName) {
		String json = format("{\"group\":\"%s\"}", groupIdOrName);
		String url = format("/manage/v2/hosts/%s/properties", hostIdOrName);
		return manageClient.putJson(url, json);
	}

	public String getAssignedGroupName(String hostIdOrName) {
		String url = format("/manage/v2/hosts/%s/properties", hostIdOrName);
		return payloadParser.getPayloadFieldValue(manageClient.getJson(url), "group");
	}
}
