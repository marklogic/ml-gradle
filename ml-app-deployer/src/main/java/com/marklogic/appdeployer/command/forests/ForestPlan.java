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

import com.marklogic.mgmt.api.forest.Forest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ForestPlan {

	private String databaseName;
	private List<String> hostNames;
	private List<String> replicaHostNames;
	private String template;
	private int forestsPerDataDirectory = 1;
	private List<Forest> existingForests = new ArrayList<>();
	private int replicaCount = 0;
	private Map<String, String> hostsToZones;

	public ForestPlan(String databaseName, String... hostNames) {
		this(databaseName, Arrays.asList(hostNames));
	}

	/**
	 * @param databaseName
	 * @param hostNames    the list of hosts that primary and replica forests can be created on. If the list of replica
	 *                     forest host names differs, use withReplicaHostNames
	 */
	public ForestPlan(String databaseName, List<String> hostNames) {
		this.databaseName = databaseName;
		this.hostNames = hostNames;
		this.replicaHostNames = hostNames;
	}

	public ForestPlan withTemplate(String template) {
		this.template = template;
		return this;
	}

	public ForestPlan withForestsPerDataDirectory(int count) {
		this.forestsPerDataDirectory = count;
		return this;
	}

	public ForestPlan withExistingForests(List<Forest> existingForests) {
		this.existingForests = existingForests;
		return this;
	}

	public ForestPlan withReplicaCount(int count) {
		this.replicaCount = count;
		return this;
	}

	/**
	 * Confusingly, this is only used - at least as of 6.0.0 - when previewing forest creation. It is not used when
	 * actually configuring forest replicas.
	 *
	 * @param replicaHostNames
	 * @return
	 */
	public ForestPlan withReplicaHostNames(List<String> replicaHostNames) {
		this.replicaHostNames = replicaHostNames;
		return this;
	}

	/**
	 * @param hostsToZones a mapping of each host name to an optional zone value for each host. The zone value can be
	 *                     null for a host.
	 * @since 6.0.0
	 */
	public ForestPlan withHostsToZones(Map<String, String> hostsToZones) {
		this.hostsToZones = hostsToZones;
		return this;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public List<String> getHostNames() {
		return hostNames;
	}

	public int getForestsPerDataDirectory() {
		return forestsPerDataDirectory;
	}

	public int getReplicaCount() {
		return replicaCount;
	}

	public String getTemplate() {
		return template;
	}

	public List<Forest> getExistingForests() {
		return existingForests;
	}

	public List<String> getReplicaHostNames() {
		return replicaHostNames;
	}

	/**
	 * @return
	 * @since 6.0.0
	 */
	public Map<String, String> getHostsToZones() {
		return hostsToZones;
	}
}
