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

