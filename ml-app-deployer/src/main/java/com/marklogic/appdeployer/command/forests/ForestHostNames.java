/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.command.forests;

import java.util.List;

// This is no longer needed now that it doesn't capture replica host names as well.
// Will remove this in favor of a List<String> in the next PR.
public class ForestHostNames {

	private List<String> primaryForestHostNames;

	public ForestHostNames(List<String> primaryForestHostNames) {
		this.primaryForestHostNames = primaryForestHostNames;
	}

	public List<String> getPrimaryForestHostNames() {
		return primaryForestHostNames;
	}
}
