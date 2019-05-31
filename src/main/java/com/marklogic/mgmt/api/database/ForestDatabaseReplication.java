package com.marklogic.mgmt.api.database;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class ForestDatabaseReplication {

	@XmlElementWrapper(name = "foreign-replicas")
	@XmlElement(name = "foreign-replica")
	private List<ForestForeignReplica> foreignReplica;

	@XmlElement(name = "foreign-master")
	private ForestForeignReplica foreignMaster;

	public List<ForestForeignReplica> getForeignReplica() {
		return foreignReplica;
	}

	public void setForeignReplica(List<ForestForeignReplica> foreignReplica) {
		this.foreignReplica = foreignReplica;
	}

	public ForestForeignReplica getForeignMaster() {
		return foreignMaster;
	}

	public void setForeignMaster(ForestForeignReplica foreignMaster) {
		this.foreignMaster = foreignMaster;
	}
}
