package com.marklogic.mgmt.api.security.protectedpath;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class PathNamespace {

	private String prefix;

	@XmlElement(name = "namespace-uri")
	private String namespaceUri;

	public PathNamespace() {
	}

	public PathNamespace(String prefix, String namespaceUri) {
		this.prefix = prefix;
		this.namespaceUri = namespaceUri;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getNamespaceUri() {
		return namespaceUri;
	}

	public void setNamespaceUri(String namespaceUri) {
		this.namespaceUri = namespaceUri;
	}
}
