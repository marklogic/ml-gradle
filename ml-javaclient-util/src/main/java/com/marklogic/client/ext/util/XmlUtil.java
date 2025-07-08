/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.ext.util;

import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaders;
import org.xml.sax.InputSource;

import java.io.StringReader;

public abstract class XmlUtil {

	public static SAXBuilder newSAXBuilder() {
		SAXBuilder builder = new SAXBuilder(XMLReaders.NONVALIDATING);

		// Prevent DTDs from being loaded
		builder.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);

		// Disable external entities
		builder.setFeature("http://xml.org/sax/features/external-general-entities", false);
		builder.setFeature("http://xml.org/sax/features/external-parameter-entities", false);

		// Set a no-op EntityResolver to block external DTDs
		builder.setEntityResolver((publicId, systemId) -> new InputSource(new StringReader("")));

		return builder;
	}
	
	private XmlUtil() {
	}
}
