package com.marklogic.mgmt.api.database;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class ElementAttributeIndex extends ElementIndex {

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
