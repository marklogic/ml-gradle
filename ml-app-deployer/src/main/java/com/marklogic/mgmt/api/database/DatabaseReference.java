/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt.api.database;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

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
