package com.marklogic.client.ext.spring;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.ext.helper.DatabaseClientConfig;
import com.marklogic.client.ext.helper.LoggingObject;

/**
 * Hooks into Spring container lifecycle so that the DatabaseClient is initialized when the container starts up and
 * released when the container shuts down.
 * <p>
 * Note that ML7 has a DatabaseClientFactory.Bean that removes the need for most of this code, although it does not have
 * a "destroy" method that would handle releasing the DatabaseClient that it begins.
 */
public class DatabaseClientManager extends LoggingObject implements FactoryBean<DatabaseClient>, DisposableBean {

    private DatabaseClientConfig config;
    private DatabaseClient client;

    public DatabaseClientManager() {
        super();
    }

    public DatabaseClientManager(DatabaseClientConfig config) {
        this();
        this.config = config;
    }

    @Override
    public DatabaseClient getObject() {
        if (client == null) {
            if (logger.isInfoEnabled()) {
                logger.info("Connecting to REST server with: " + config);
            }
            client = DatabaseClientFactory.newClient(config.getHost(), config.getPort(), config.getDatabase(),
                    config.getUsername(), config.getPassword(), config.getAuthentication(), config.getSslContext(),
                    config.getSslHostnameVerifier());
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
