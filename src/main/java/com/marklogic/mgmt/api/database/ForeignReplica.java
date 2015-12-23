package com.marklogic.mgmt.api.database;

public class ForeignReplica {

    private String foreignClusterName;
    private String foreignDatabaseName;
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
