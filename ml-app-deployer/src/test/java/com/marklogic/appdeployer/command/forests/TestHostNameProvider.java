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
