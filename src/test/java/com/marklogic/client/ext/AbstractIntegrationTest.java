package com.marklogic.client.ext;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.ext.spring.SpringDatabaseClientConfig;
import org.junit.After;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
public abstract class AbstractIntegrationTest extends Assert {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	protected DatabaseClientConfig clientConfig;
	protected DatabaseClient client;

	protected ConfiguredDatabaseClientFactory configuredDatabaseClientFactory = new DefaultConfiguredDatabaseClientFactory();

	protected DatabaseClient newClient() {
		client = configuredDatabaseClientFactory.newDatabaseClient(clientConfig);
		return client;
	}

	@After
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
@PropertySource("classpath:application.properties")
class TestConfig {

	/**
	 * Ensures that placeholders are replaced with property values
	 */
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyPlaceHolderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
}
