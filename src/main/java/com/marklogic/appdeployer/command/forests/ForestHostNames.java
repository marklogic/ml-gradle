package com.marklogic.appdeployer.command.forests;

import java.util.List;

public class ForestHostNames {

	private List<String> primaryForestHostNames;
	private List<String> replicaForestHostNames;

	public ForestHostNames(List<String> primaryForestHostNames, List<String> replicaForestHostNames) {
		this.primaryForestHostNames = primaryForestHostNames;
		this.replicaForestHostNames = replicaForestHostNames;
	}

	public List<String> getPrimaryForestHostNames() {
		return primaryForestHostNames;
	}

	public List<String> getReplicaForestHostNames() {
		return replicaForestHostNames;
	}
}
