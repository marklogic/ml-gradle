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

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Encapsulates an XML fragment with a variety of methods for assisting with XPath-based assertions in a JUnit test.
 */
public class Fragment {

	protected final Logger logger = LoggerFactory.getLogger(getClass());
	private Document internalDoc;
	private Namespace[] namespaces;
	private String uri;

	public Fragment(Document doc) {
		this.internalDoc = doc;
	}

	public Fragment(String xml, Namespace... namespaces) {
		try {
			internalDoc = new SAXBuilder().build(new StringReader(xml));
			this.namespaces = namespaces;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public Fragment(String uri, String xml, Namespace... namespaces) {
		this(xml, namespaces);
		this.uri = uri;
	}

	public Fragment(Fragment other) {
		this.internalDoc = other.internalDoc;
		this.namespaces = other.namespaces;
		this.uri = other.uri;
	}

	public Fragment(Element el, Namespace... namespaces) {
		this.internalDoc = new Document(el.detach());
		this.namespaces = namespaces;
	}

	protected String format(String format, Object... args) {
		return String.format(format, args);
	}

	public Fragment getFragment(String xpath) {
		List<Element> list = evaluateForElements(xpath);
		try {
			assertElementListHasOneElement("Expected to find a single element with xpath: " + xpath, list, xpath);
			return new Fragment(list.get(0), this.namespaces);
		} catch (AssertionError ae) {
			prettyPrint();
			throw ae;
		}
	}

	public List<Fragment> getFragments(String xpath) {
		List<Element> elements = evaluateForElements(xpath);
		List<Fragment> fragments = new ArrayList<>();
		for (Element el : elements) {
			fragments.add(new Fragment(el, this.namespaces));
		}
		return fragments;
	}

	public void assertElementValue(String xpath, String value) {
		assertElementValue("Could not find element with value", xpath, value);
	}

	public String getElementValue(String xpath) {
		List<Element> list = evaluateForElements(xpath);
		try {
			assertElementListHasOneElement("", list, xpath);
			return list.get(0).getText();
		} catch (AssertionError ae) {
			prettyPrint();
			throw ae;
		}
	}

	/**
	 * Seemingly can't use XPath in JDOM2 to get an attribute value directly.
	 */
	public String getAttributeValue(String elementXpath, String attributeName) {
		List<Element> list = evaluateForElements(elementXpath);
		try {
			assertElementListHasOneElement("", list, elementXpath);
			return list.get(0).getAttributeValue(attributeName);
		} catch (AssertionError ae) {
			prettyPrint();
			throw ae;
		}
	}

	public void assertElementValue(String message, String xpath, String value) {
		List<Element> list = evaluateForElements(xpath);
		try {
			assertTrue(list.size() > 0, message += ";\nCould not find at least one element, xpath: " + xpath);
			boolean found = false;
			for (Element el : list) {
				if (value.equals(el.getText())) {
					found = true;
					break;
				}
			}
			assertTrue(found, message + "\n:Elements: " + list);
		} catch (AssertionError ae) {
			prettyPrint();
			throw ae;
		}
	}

	public void assertElementCount(String message, String xpath, int count) {
		String xpathToTest = xpath + "[%d]";
		assertElementExists(message, format(xpathToTest, count));
		assertElementMissing(message, format(xpathToTest, count + 1));
	}

	private void assertElementListHasOneElement(String message, List<Element> list, String xpath) {
		int size = list.size();
		assertEquals(1, size, message + ";\nExpected 1 element, but found " + size + "; xpath: " + xpath);
	}

	public void assertElementExists(String xpath) {
		assertElementExists("", xpath);
	}

	public void assertElementExists(String message, String xpath) {
		List<Element> list = evaluateForElements(xpath);
		try {
			assertElementListHasOneElement(message, list, xpath);
		} catch (AssertionError ae) {
			prettyPrint();
			throw ae;
		}
	}

	public void assertElementMissing(String message, String xpath) {
		List<Element> list = evaluateForElements(xpath);
		assertEquals(0, list.size(), message + ";\nexpected no elements matching xpath " + xpath);
	}

	protected List<Element> evaluateForElements(String xpath) {
		XPathFactory f = XPathFactory.instance();
		XPathExpression<Element> expr = f.compile(xpath, Filters.element(), new HashMap<>(), namespaces);
		return expr.evaluate(internalDoc);
	}

	public void prettyPrint() {
		logger.info(getPrettyXml());
	}

	public String getPrettyXml() {
		return new XMLOutputter(Format.getPrettyFormat()).outputString(internalDoc);
	}

	public String getUri() {
		return uri;
	}

	public Document getInternalDoc() {
		return internalDoc;
	}

	public Namespace[] getNamespaces() {
		return namespaces;
	}

	public void setNamespaces(Namespace[] namespaces) {
		this.namespaces = namespaces;
	}
}
