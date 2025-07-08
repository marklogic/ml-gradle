/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt.api.database;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class ForeignReplica {

	@XmlElement(name = "foreign-cluster-name")
	private String foreignClusterName;

	@XmlElement(name = "foreign-database-name")
	private String foreignDatabaseName;

	@XmlElement(name = "connect-forests-by-name")
	private Boolean connectForestsByName;

	@XmlElement(name = "lag-limit")
	private Integer lagLimit;

	@XmlElement(name = "replication-enabled")
	private Boolean replicationEnabled;

	@XmlElement(name = "queue-size")
	private Integer queueSize;

	public String getForeignClusterName() {
		return foreignClusterName;
	}

	public void setForeignClusterName(String foreignClusterName) {
		this.foreignClusterName = foreignClusterName;
	}

	public String getForeignDatabaseName() {
		return foreignDatabaseName;
	}

	public void setForeignDatabaseName(String foreignDatabaseName) {
		this.foreignDatabaseName = foreignDatabaseName;
	}

	public Boolean getConnectForestsByName() {
		return connectForestsByName;
	}

	public void setConnectForestsByName(Boolean connectForestsByName) {
		this.connectForestsByName = connectForestsByName;
	}

	public Integer getLagLimit() {
		return lagLimit;
	}

	public void setLagLimit(Integer lagLimit) {
		this.lagLimit = lagLimit;
	}

	public Boolean getReplicationEnabled() {
		return replicationEnabled;
	}

	public void setReplicationEnabled(Boolean replicationEnabled) {
		this.replicationEnabled = replicationEnabled;
	}

	public Integer getQueueSize() {
		return queueSize;
	}

	public void setQueueSize(Integer queueSize) {
		this.queueSize = queueSize;
	}
}
