/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt.api.forest;

import com.marklogic.mgmt.api.ApiObject;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class ForestReplica extends ApiObject {

	private String host;

	@XmlElement(name = "replica-name")
	private String replicaName;

	@XmlElement(name = "data-directory")
	private String dataDirectory;

	@XmlElement(name = "large-data-directory")
	private String largeDataDirectory;

	@XmlElement(name = "fast-data-directory")
	private String fastDataDirectory;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getReplicaName() {
		return replicaName;
	}

	public void setReplicaName(String replicaName) {
		this.replicaName = replicaName;
	}

	public String getDataDirectory() {
		return dataDirectory;
	}

	public void setDataDirectory(String dataDirectory) {
		this.dataDirectory = dataDirectory;
	}

	public String getLargeDataDirectory() {
		return largeDataDirectory;
	}

	public void setLargeDataDirectory(String largeDataDirectory) {
		this.largeDataDirectory = largeDataDirectory;
	}

	public String getFastDataDirectory() {
		return fastDataDirectory;
	}

	public void setFastDataDirectory(String fastDataDirectory) {
		this.fastDataDirectory = fastDataDirectory;
	}

}
