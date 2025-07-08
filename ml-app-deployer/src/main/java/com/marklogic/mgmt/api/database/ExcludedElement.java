/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt.api.database;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class ExcludedElement extends Element {

	@XmlElement(name = "attribute-namespace-uri")
	private String attributeNamespaceUri;

	@XmlElement(name = "attribute-localname")
	private String attributeLocalname;

	@XmlElement(name = "attribute-value")
	private String attributeValue;

	public String getAttributeNamespaceUri() {
		return attributeNamespaceUri;
	}

	public void setAttributeNamespaceUri(String attributeNamespaceUri) {
		this.attributeNamespaceUri = attributeNamespaceUri;
	}

	public String getAttributeLocalname() {
		return attributeLocalname;
	}

	public void setAttributeLocalname(String attributeLocalname) {
		this.attributeLocalname = attributeLocalname;
	}

	public String getAttributeValue() {
		return attributeValue;
	}

	public void setAttributeValue(String attributeValue) {
		this.attributeValue = attributeValue;
	}
}
