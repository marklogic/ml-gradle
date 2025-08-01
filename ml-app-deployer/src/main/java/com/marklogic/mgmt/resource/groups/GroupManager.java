/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
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
