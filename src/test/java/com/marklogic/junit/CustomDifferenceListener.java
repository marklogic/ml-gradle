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

	private List<Difference> differences = new ArrayList<>();

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
