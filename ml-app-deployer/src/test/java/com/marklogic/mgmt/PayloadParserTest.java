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
