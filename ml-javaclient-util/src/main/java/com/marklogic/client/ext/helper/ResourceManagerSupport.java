/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.ext.helper;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.extensions.ResourceManager;

public abstract class ResourceManagerSupport extends ResourceManager implements ResourceExtension {

    protected abstract String getResourceName();

    private DatabaseClient client;

    protected DatabaseClient getClient() {
        return this.client;
    }

    @Override
    public void setDatabaseClient(DatabaseClient client) {
        this.client = client;
        client.init(getResourceName(), this);
    }
}
