package com.marklogic.clientutil;

import com.marklogic.client.DatabaseClient;

public interface DatabaseClientProvider {

    public DatabaseClient getDatabaseClient();
}
