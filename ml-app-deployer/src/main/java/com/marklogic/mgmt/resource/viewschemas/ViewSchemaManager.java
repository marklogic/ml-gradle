/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt.resource.viewschemas;

import com.marklogic.mgmt.resource.AbstractResourceManager;
import com.marklogic.mgmt.ManageClient;

/**
 * This class requires a database ID or name so that it can build view-schema URLs for that particular database.
 */
public class ViewSchemaManager extends AbstractResourceManager {

    private String databaseIdOrName;

    public ViewSchemaManager(ManageClient client, String databaseIdOrName) {
        super(client);
        this.databaseIdOrName = databaseIdOrName;
    }

    @Override
    protected String getResourceName() {
        return "view-schema";
    }

    @Override
    protected String getIdFieldName() {
        return "view-schema-name";
    }

    @Override
    public String getResourcesPath() {
        return format("/manage/v2/databases/%s/view-schemas", databaseIdOrName);
    }

}
