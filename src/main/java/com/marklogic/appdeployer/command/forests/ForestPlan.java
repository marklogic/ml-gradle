package com.marklogic.appdeployer.command.forests;

import com.marklogic.mgmt.api.forest.Forest;

import java.util.Arrays;
import java.util.List;

public class ForestPlan {

	private String databaseName;
	private List<String> hostNames;
	private String template;
	private int forestsPerDataDirectory = 1;
	private int existingForestsPerDataDirectory = 0;
	private int replicaCount = 0;

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

	public ForestPlan withExistingForestsPerDataDirectory(int count) {
		this.existingForestsPerDataDirectory = count;
		return this;
	}

	public ForestPlan withReplicaCount(int count) {
		this.replicaCount = count;
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

	public int getExistingForestsPerDataDirectory() {
		return existingForestsPerDataDirectory;
	}

	public int getReplicaCount() {
		return replicaCount;
	}

	public String getTemplate() {
		return template;
	}
}
