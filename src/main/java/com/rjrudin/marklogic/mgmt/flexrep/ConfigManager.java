package com.rjrudin.marklogic.mgmt.flexrep;

import com.rjrudin.marklogic.mgmt.AbstractResourceManager;
import com.rjrudin.marklogic.mgmt.ManageClient;

public class ConfigManager extends AbstractResourceManager {

    private String databaseIdOrName;

    public ConfigManager(ManageClient client, String databaseIdOrName) {
        super(client);
        this.databaseIdOrName = databaseIdOrName;
    }

    @Override
    public String getResourcesPath() {
        return format("/manage/v2/databases/%s/flexrep/configs", databaseIdOrName);
    }

    @Override
    protected String getIdFieldName() {
        return "domain-name";
    }

}
