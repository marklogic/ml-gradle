/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt.api.database;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class Element {

	@XmlElement(name = "namespace-uri")
	private String namespaceUri;

	/**
	 * As of ML 10.0-2, this won't work for both JSON and XML. When multiple values exist, the Manage API expects the
	 * JSON representation to be an array of strings, while the XML representation always expects a single value.
	 */
	@XmlElementWrapper(name = "localname")
	private List<String> localname;

	public String getNamespaceUri() {
		return namespaceUri;
	}

	public void setNamespaceUri(String namespaceUri) {
		this.namespaceUri = namespaceUri;
	}

	public List<String> getLocalname() {
		return localname;
	}

	public void setLocalname(List<String> localname) {
		this.localname = localname;
	}

}
