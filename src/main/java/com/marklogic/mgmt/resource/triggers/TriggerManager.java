package com.marklogic.mgmt.resource.triggers;

import com.marklogic.mgmt.resource.AbstractResourceManager;
import com.marklogic.mgmt.ManageClient;

public class TriggerManager extends AbstractResourceManager {

    private String databaseIdOrName;

    public TriggerManager(ManageClient client, String databaseIdOrName) {
        super(client);
        this.databaseIdOrName = databaseIdOrName;
    }

    @Override
    public String getResourcesPath() {
        return format("/manage/v2/databases/%s/triggers", databaseIdOrName);
    }

    @Override
    protected String getIdFieldName() {
        return "name";
    }

}
