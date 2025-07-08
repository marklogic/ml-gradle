/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.command.forests;

import com.marklogic.mgmt.resource.hosts.HostNameProvider;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestHostNameProvider implements HostNameProvider {

	private List<String> hostNames;
	private Map<String, List<String>> groupHostNames = new HashMap<>();

	public TestHostNameProvider(String... hostNames) {
		this.hostNames = Arrays.asList(hostNames);
	}

	public void addGroupHostNames(String groupName, String... hostNames) {
		groupHostNames.put(groupName, Arrays.asList(hostNames));
	}

	@Override
	public List<String> getHostNames() {
		return this.hostNames;
	}

	@Override
	public List<String> getGroupHostNames(String groupName) {
		return groupHostNames.get(groupName);
	}
}
