package com.marklogic.junit;

import java.util.ArrayList;
import java.util.List;

import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.DifferenceEngine;
import org.custommonkey.xmlunit.DifferenceListener;
import org.w3c.dom.Node;

import com.marklogic.client.ext.helper.LoggingObject;

/**
 * <code>DifferenceListener</code> implementation that captures all differences in a list which can then be retrieved.
 */
public class CustomDifferenceListener extends LoggingObject implements DifferenceListener {

	private List<Difference> differences = new ArrayList<Difference>();

	@Override
	public int differenceFound(Difference difference) {
		differences.add(difference);
		if (difference.getId() == DifferenceEngine.NAMESPACE_PREFIX_ID) {
			return RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL;
		}
		return RETURN_ACCEPT_DIFFERENCE;
	}

	@Override
	public void skippedComparison(Node control, Node test) {
	}

	public List<Difference> getDifferences() {
		return differences;
	}

}
