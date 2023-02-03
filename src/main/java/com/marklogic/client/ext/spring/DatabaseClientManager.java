/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
