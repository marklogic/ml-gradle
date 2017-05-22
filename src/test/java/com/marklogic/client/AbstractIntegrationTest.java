package com.marklogic.client;

import com.marklogic.client.helper.DatabaseClientConfig;
import com.marklogic.client.spring.config.MarkLogicApplicationContext;
import org.junit.After;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration( classes = {MarkLogicApplicationContext.class} )
public abstract class AbstractIntegrationTest extends Assert {

	@Autowired
	protected DatabaseClientConfig clientConfig;
	protected DatabaseClient client;

	protected DatabaseClient newClient() {
		client = DatabaseClientFactory.newClient(clientConfig.getHost(), clientConfig.getPort(), clientConfig.getUsername(),
			clientConfig.getPassword(), DatabaseClientFactory.Authentication.DIGEST);
		return client;
	}

	protected DatabaseClient newClient(String database) {
		client = DatabaseClientFactory.newClient(clientConfig.getHost(), clientConfig.getPort(), database, clientConfig.getUsername(),
			clientConfig.getPassword(), DatabaseClientFactory.Authentication.DIGEST);
		return client;
	}

	@After
	public void releaseClient() {
		if (client != null) {
			client.release();
		}
	}


}
