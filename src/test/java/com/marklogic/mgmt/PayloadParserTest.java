package com.marklogic.mgmt;

import com.marklogic.junit.BaseTestHelper;
import com.marklogic.junit.Fragment;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PayloadParserTest extends BaseTestHelper {

	PayloadParser parser = new PayloadParser();

	@Test
	public void simpleRemove() {
		String xml = "<test>" +
			"<hello>world</hello>" +
			"</test>";
		String output = parser.excludeProperties(xml, "/test/hello");
		assertFragmentIsIdentical(new Fragment(output), "<test/>");
	}

	@Test
	public void simpleRemoveWithNamespace() {
		String xml = "<test xmlns='test'>" +
			"<hello>world</hello>" +
			"</test>";

		String output = parser.excludeProperties(xml, "/node()/node()[local-name(.) = 'hello']");
		assertFragmentIsIdentical(new Fragment(output), "<test xmlns='test'/>");
	}

	@Test
	public void removeMultipleWithNamespaces() {
		String xml = "<test xmlns='test'>" +
			"<hello>world</hello>" +
			"<hello>again</hello>" +
			"<keep>this</keep>" +
			"</test>";

		String output = parser.excludeProperties(xml, "/node()/node()[local-name(.) = 'hello']");
		assertEquals("<test xmlns=\"test\"><keep>this</keep></test>", output);
	}
}
