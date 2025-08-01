/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt.api.database;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class ElementAttributeWordLexicon extends ElementWordLexicon {

	@XmlElement(name = "parent-namespace-uri")
	private String parentNamespaceUri;

	@XmlElement(name = "parent-localname")
	private String parentLocalname;

	public String getParentNamespaceUri() {
		return parentNamespaceUri;
	}

	public void setParentNamespaceUri(String parentNamespaceUri) {
		this.parentNamespaceUri = parentNamespaceUri;
	}

	public String getParentLocalname() {
		return parentLocalname;
	}

	public void setParentLocalname(String parentLocalname) {
		this.parentLocalname = parentLocalname;
	}
}
