package com.rjrudin.marklogic.mgmt.flexrep;

import com.rjrudin.marklogic.mgmt.AbstractResourceManager;
import com.rjrudin.marklogic.mgmt.ManageClient;

public class FlexrepConfigManager extends AbstractResourceManager {

    private String databaseIdOrName;

    public FlexrepConfigManager(ManageClient client, String databaseIdOrName) {
        super(client);
        this.databaseIdOrName = databaseIdOrName;
    }

    @Override
    public String getResourcesPath() {
        return format("/manage/v2/databases/%s/flexrep/configs", databaseIdOrName);
    }

    @Override
    public String getResourcePath(String resourceNameOrId) {
        return super.getResourcePath(resourceNameOrId);
    }

    @Override
    protected String getIdFieldName() {
        return "domain-name";
    }

}
