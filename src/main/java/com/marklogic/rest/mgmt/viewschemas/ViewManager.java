package com.marklogic.rest.mgmt.viewschemas;

import com.marklogic.rest.mgmt.AbstractResourceManager;
import com.marklogic.rest.mgmt.ManageClient;

public class ViewManager extends AbstractResourceManager {

    private String databaseIdOrName;
    private String schemaName;

    public ViewManager(ManageClient client, String databaseIdOrName, String schemaName) {
        super(client);
        this.databaseIdOrName = databaseIdOrName;
        this.schemaName = schemaName;
    }

    @Override
    public String getResourcesPath() {
        return format("/manage/v2/databases/%s/view-schemas/%s/views", databaseIdOrName, schemaName);
    }

}
