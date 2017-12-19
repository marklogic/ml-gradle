package com.marklogic.client.ext;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.ext.spring.config.MarkLogicApplicationContext;
import org.junit.After;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration( classes = {MarkLogicApplicationContext.class} )
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

	protected DatabaseClient newClient(String database) {
		String currentDatabase = clientConfig.getDatabase();
		clientConfig.setDatabase(database);
		client = configuredDatabaseClientFactory.newDatabaseClient(clientConfig);
		clientConfig.setDatabase(currentDatabase);
		return client;
	}
}
