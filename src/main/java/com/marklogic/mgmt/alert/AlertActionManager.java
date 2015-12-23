package com.marklogic.mgmt.alert;

import com.marklogic.mgmt.AbstractResourceManager;
import com.marklogic.mgmt.ManageClient;

public class AlertActionManager extends AbstractResourceManager {

    private String databaseIdOrName;
    private String configUri;

    public AlertActionManager(ManageClient client, String databaseIdOrName, String configUri) {
        super(client);
        this.databaseIdOrName = databaseIdOrName;
        this.configUri = configUri;
    }

    @Override
    public String getResourcesPath() {
        return format("/manage/v2/databases/%s/alert/actions?uri=%s", databaseIdOrName, configUri);
    }

    @Override
    public String getResourcePath(String resourceNameOrId, String... resourceUrlParams) {
        return format("/manage/v2/databases/%s/alert/actions/%s?uri=%s", databaseIdOrName, resourceNameOrId, configUri);
    }

    @Override
    public String getPropertiesPath(String resourceNameOrId, String... resourceUrlParams) {
        return format("/manage/v2/databases/%s/alert/actions/%s/properties?uri=%s", databaseIdOrName, resourceNameOrId,
                configUri);
    }

    @Override
    protected String getIdFieldName() {
        return "name";
    }
}
