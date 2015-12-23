package com.marklogic.client.helper;

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
