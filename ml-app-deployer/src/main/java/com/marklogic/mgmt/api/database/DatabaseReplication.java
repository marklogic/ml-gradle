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
public class DatabaseReplication {

	@XmlElementWrapper(name = "foreign-replicas")
	@XmlElement(name = "foreign-replica")
	private List<ForeignReplica> foreignReplica;

	@XmlElement(name = "foreign-master")
	private ForeignMaster foreignMaster;

	public List<ForeignReplica> getForeignReplica() {
		return foreignReplica;
	}

	public void setForeignReplica(List<ForeignReplica> foreignReplica) {
		this.foreignReplica = foreignReplica;
	}

	public ForeignMaster getForeignMaster() {
		return foreignMaster;
	}

	public void setForeignMaster(ForeignMaster foreignMaster) {
		this.foreignMaster = foreignMaster;
	}
}
