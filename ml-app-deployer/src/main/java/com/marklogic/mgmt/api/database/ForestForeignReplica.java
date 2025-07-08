/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt.api.database;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class ForestForeignReplica {

	@XmlElement(name = "foreign-cluster-name")
	private String foreignClusterName;

	@XmlElement(name = "foreign-database-name")
	private String foreignDatabaseName;

	@XmlElement(name = "foreign-forest-name")
	private String foreignForestName;

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

    public String getForeignForestName() {
        return foreignForestName;
    }

    public void setForeignForestName(String foreignForestName) {
        this.foreignForestName = foreignForestName;
    }

}
