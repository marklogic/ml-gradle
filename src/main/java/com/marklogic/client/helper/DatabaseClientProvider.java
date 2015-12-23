package com.marklogic.client.helper;

import com.marklogic.client.DatabaseClient;

public interface DatabaseClientProvider {

    public DatabaseClient getDatabaseClient();
}
