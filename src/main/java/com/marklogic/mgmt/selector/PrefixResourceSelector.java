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
