package com.marklogic.client.ext.helper;

import com.marklogic.client.DatabaseClient;

public interface DatabaseClientProvider {

    public DatabaseClient getDatabaseClient();
}
