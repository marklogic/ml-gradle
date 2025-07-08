/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PayloadParserTest {

	private PayloadParser parser = new PayloadParser();

	@Test
	void simpleRemove() {
		String xml = "<test>" +
			"<hello>world</hello>" +
			"</test>";
		String output = parser.excludeProperties(xml, "/test/hello");
		assertEquals("<test />", output);
	}

	@Test
	void simpleRemoveWithNamespace() {
		String xml = "<test xmlns='test'>" +
			"<hello>world</hello>" +
			"</test>";

		String output = parser.excludeProperties(xml, "/node()/node()[local-name(.) = 'hello']");
		assertEquals("<test xmlns=\"test\" />", output);
	}

	@Test
	void removeMultipleWithNamespaces() {
		String xml = "<test xmlns='test'>" +
			"<hello>world</hello>" +
			"<hello>again</hello>" +
			"<keep>this</keep>" +
			"</test>";

		String output = parser.excludeProperties(xml, "/node()/node()[local-name(.) = 'hello']");
		assertEquals("<test xmlns=\"test\"><keep>this</keep></test>", output);
	}
}
