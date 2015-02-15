package com.marklogic.clientutil.spring;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.marklogic.client.DatabaseClient;
import com.marklogic.clientutil.DatabaseClientConfig;
import com.marklogic.clientutil.DatabaseClientProvider;

public class SimpleDatabaseClientProvider implements DatabaseClientProvider, DisposableBean {

    @Autowired
    private DatabaseClientConfig config;

    private DatabaseClient client;

    @Override
    public DatabaseClient getDatabaseClient() {
        if (client == null) {
            DatabaseClientManager mgr = new DatabaseClientManager();
            mgr.setConfig(config);
            client = mgr.getObject();
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
