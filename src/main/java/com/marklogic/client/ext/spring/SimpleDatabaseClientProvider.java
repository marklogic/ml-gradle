package com.marklogic.client.ext.spring;

import org.springframework.beans.factory.DisposableBean;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.ext.DatabaseClientConfig;
import com.marklogic.client.ext.helper.DatabaseClientProvider;

public class SimpleDatabaseClientProvider implements DatabaseClientProvider, DisposableBean {

    private DatabaseClientConfig config;
    private DatabaseClient client;

    public SimpleDatabaseClientProvider() {
    }

    public SimpleDatabaseClientProvider(DatabaseClientConfig config) {
        this.config = config;
    }

    public SimpleDatabaseClientProvider(DatabaseClientManager mgr) {
        this.client = mgr.getObject();
    }

    @Override
    public DatabaseClient getDatabaseClient() {
        if (client == null) {
            client = new DatabaseClientManager(config).getObject();
        }
        return client;
    }

    @Override
    public void destroy() throws Exception {
        if (client != null) {
            client.release();
        }
    }

}
