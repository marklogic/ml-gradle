/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt.resource.hosts;

import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.resource.groups.GroupManager;

import java.util.List;

public class DefaultHostNameProvider implements HostNameProvider {

	private ManageClient manageClient;

	public DefaultHostNameProvider(ManageClient manageClient) {
		this.manageClient = manageClient;
	}

	@Override
	public List<String> getHostNames() {
		return new HostManager(manageClient).getHostNames();
	}

	@Override
	public List<String> getGroupHostNames(String groupName) {
		return new GroupManager(manageClient).getHostNames(groupName);
	}
}
