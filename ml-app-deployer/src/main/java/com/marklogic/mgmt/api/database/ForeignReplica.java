/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.mgmt.api.database;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

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
