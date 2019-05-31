package com.marklogic.mgmt.api.database;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class DatabaseReference {

	@XmlElement(name = "reference-cluster-name")
	private String referenceClusterName;

	@XmlElement(name = "reference-database-name")
	private String referenceDatabaseName;

	public String getReferenceClusterName() {
		return referenceClusterName;
	}

	public void setReferenceClusterName(String referenceClusterName) {
		this.referenceClusterName = referenceClusterName;
	}

	public String getReferenceDatabaseName() {
		return referenceDatabaseName;
	}

	public void setReferenceDatabaseName(String referenceDatabaseName) {
		this.referenceDatabaseName = referenceDatabaseName;
	}
}
