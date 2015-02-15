package com.marklogic.clientutil.spring;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.clientutil.DatabaseClientConfig;
import com.marklogic.clientutil.LoggingObject;

/**
 * Note that ML7 has a DatabaseClientFactory.Bean that removes the need for most of this code, although it does not have
 * a "destroy" method that would handle releasing the DatabaseClient that it begins.
 */
public class DatabaseClientManager extends LoggingObject implements FactoryBean<DatabaseClient>, DisposableBean {

    private DatabaseClientConfig config;
    private DatabaseClient client;

    @Override
    public DatabaseClient getObject() {
        if (client == null) {
            if (logger.isInfoEnabled()) {
                logger.info("Connecting to REST server with: " + config);
            }
            client = DatabaseClientFactory.newClient(config.getHost(), config.getPort(), config.getUsername(),
                    config.getPassword(), config.getAuthentication());
        }
        return client;
    }

    @Override
    public Class<?> getObjectType() {
        return DatabaseClient.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void destroy() {
        if (client != null) {
            if (logger.isInfoEnabled()) {
                logger.info("Releasing client with username: " + config.getUsername());
            }
            client.release();
        }
    }

    public void setConfig(DatabaseClientConfig config) {
        this.config = config;
    }
}
