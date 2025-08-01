/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt.resource.alert;

import com.marklogic.mgmt.resource.AbstractResourceManager;
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
