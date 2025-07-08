/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
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
	private String template;
	private int forestsPerDataDirectory = 1;
	private List<Forest> existingForests = new ArrayList<>();
	private int replicaCount = 0;
	private Map<String, String> hostsToZones;

	public ForestPlan(String databaseName, String... hostNames) {
		this(databaseName, Arrays.asList(hostNames));
	}

	public ForestPlan(String databaseName, List<String> hostNames) {
		this.databaseName = databaseName;
		this.hostNames = hostNames;
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

	/**
	 * @return
	 * @since 6.0.0
	 */
	public Map<String, String> getHostsToZones() {
		return hostsToZones;
	}
}
