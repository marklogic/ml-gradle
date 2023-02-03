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
package com.marklogic.client.ext;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.ext.spring.SpringDatabaseClientConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfig.class})
public abstract class AbstractIntegrationTest {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	protected DatabaseClientConfig clientConfig;
	protected DatabaseClient client;

	protected ConfiguredDatabaseClientFactory configuredDatabaseClientFactory = new DefaultConfiguredDatabaseClientFactory();

	protected DatabaseClient newClient() {
		client = configuredDatabaseClientFactory.newDatabaseClient(clientConfig);
		return client;
	}

	@AfterEach
	public void releaseClientOnTearDown() {
		if (client != null) {
			try {
				client.release();
			} catch (Exception ex) {
				// That's fine, the test probably released it already
			}
		}
	}

	protected DatabaseClient newClient(String database) {
		String currentDatabase = clientConfig.getDatabase();
		clientConfig.setDatabase(database);
		client = configuredDatabaseClientFactory.newDatabaseClient(clientConfig);
		clientConfig.setDatabase(currentDatabase);
		return client;
	}
}

@Configuration
@Import(value = {SpringDatabaseClientConfig.class})
@PropertySource(value = { "classpath:test.properties", "classpath:user.properties" }, ignoreResourceNotFound = true)
class TestConfig {

	/**
	 * Ensures that placeholders are replaced with property values
	 */
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyPlaceHolderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
}
