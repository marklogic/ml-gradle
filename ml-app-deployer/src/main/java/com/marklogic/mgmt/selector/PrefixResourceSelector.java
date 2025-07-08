/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt.selector;

public class PrefixResourceSelector extends AbstractNameMatchingResourceSelector {

	private String prefix;

	public PrefixResourceSelector(String prefix) {
		this.prefix = prefix;
	}

	@Override
	protected boolean nameMatches(String resourceName) {
		return resourceName.startsWith(prefix);
	}
}
