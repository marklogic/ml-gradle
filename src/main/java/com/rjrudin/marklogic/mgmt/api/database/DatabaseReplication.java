package com.rjrudin.marklogic.mgmt.api.database;

import java.util.List;

public class DatabaseReplication {

    private List<ForeignReplica> foreignReplica;
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
