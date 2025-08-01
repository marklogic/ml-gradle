/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.ext.spring;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.ext.DatabaseClientConfig;
import com.marklogic.client.ext.DefaultConfiguredDatabaseClientFactory;
import com.marklogic.client.ext.helper.LoggingObject;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;

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
			client = new DefaultConfiguredDatabaseClientFactory().newDatabaseClient(config);
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
