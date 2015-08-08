package com.rjrudin.marklogic.client;

import com.marklogic.client.DatabaseClient;

public interface DatabaseClientProvider {

    public DatabaseClient getDatabaseClient();
}
