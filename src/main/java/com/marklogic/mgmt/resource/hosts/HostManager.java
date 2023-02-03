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

    private ManageClient client;

    public HostManager(ManageClient client) {
        this.client = client;
    }

    public List<String> getHostIds() {
        return getHosts().getElementValues("/h:host-default-list/h:list-items/h:list-item/h:idref");
    }

    public List<String> getHostNames() {
        return getHosts().getElementValues("/h:host-default-list/h:list-items/h:list-item/h:nameref");
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
        return client.getXml("/manage/v2/hosts");
    }

    public ResponseEntity<String> setHostToGroup(String hostIdOrName, String groupIdOrName) {
        String json = format("{\"group\":\"%s\"}", groupIdOrName);
        String url = format("/manage/v2/hosts/%s/properties", hostIdOrName);
        return client.putJson(url, json);
    }

	public String getAssignedGroupName(String hostIdOrName) {
		String url = format("/manage/v2/hosts/%s/properties", hostIdOrName);
		return payloadParser.getPayloadFieldValue(client.getJson(url), "group");
	}
}
