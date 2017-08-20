package com.marklogic.mgmt.api.database;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class DatabaseReplication {

	@XmlElementWrapper(name = "foreign-replicas")
	@XmlElement(name = "foreign-replica")
	private List<ForeignReplica> foreignReplica;

	@XmlElement(name = "foreign-master")
	private ForeignReplica foreignMaster;

	public List<ForeignReplica> getForeignReplica() {
		return foreignReplica;
	}

	public void setForeignReplica(List<ForeignReplica> foreignReplica) {
		this.foreignReplica = foreignReplica;
	}

	public ForeignReplica getForeignMaster() {
		return foreignMaster;
	}

	public void setForeignMaster(ForeignReplica foreignMaster) {
		this.foreignMaster = foreignMaster;
	}
}
