/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt.selector;

import java.util.regex.Pattern;

public class RegexResourceSelector extends AbstractNameMatchingResourceSelector {

	private Pattern pattern;

	public RegexResourceSelector(String regex) {
		this.pattern = Pattern.compile(regex);
	}

	@Override
	protected boolean nameMatches(String resourceName) {
		return pattern.matcher(resourceName).matches();
	}
}
