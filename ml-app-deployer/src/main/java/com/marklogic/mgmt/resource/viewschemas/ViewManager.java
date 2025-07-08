/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt.resource.viewschemas;

import com.marklogic.mgmt.resource.AbstractResourceManager;
import com.marklogic.mgmt.ManageClient;

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
