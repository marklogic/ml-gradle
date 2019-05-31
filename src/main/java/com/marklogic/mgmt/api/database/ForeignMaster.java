package com.marklogic.mgmt.api.database;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class ForeignMaster {

	@XmlElement(name = "foreign-cluster-name")
	private String foreignClusterName;

	@XmlElement(name = "foreign-database-name")
	private String foreignDatabaseName;

	@XmlElement(name = "connect-forests-by-name")
	private Boolean connectForestsByName;

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
}
