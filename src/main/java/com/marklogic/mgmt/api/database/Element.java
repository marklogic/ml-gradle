package com.marklogic.mgmt.api.database;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class Element {

	@XmlElement(name = "namespace-uri")
	private String namespaceUri;

	private String localname;

	public String getNamespaceUri() {
		return namespaceUri;
	}

	public void setNamespaceUri(String namespaceUri) {
		this.namespaceUri = namespaceUri;
	}

	public String getLocalname() {
		return localname;
	}

	public void setLocalname(String localname) {
		this.localname = localname;
	}

}
