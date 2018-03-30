package com.marklogic.mgmt.api.database;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

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
