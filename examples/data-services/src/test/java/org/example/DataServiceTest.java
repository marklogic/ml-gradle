/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package org.example;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataServiceTest {

	@Test
	public void testMockWhatsUp() {
		assertEquals("This is a mock response", new HelloWorldMock().whatsUp("Hey!", 2L));
	}

	@Test
	public void testWhatsUp() {
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8099,
			new DatabaseClientFactory.DigestAuthContext("data-services-example-user", "password"));

		assertEquals("Hey! Hey!", HelloWorld.on(client).whatsUp("Hey!", 2L));

		client.release();
	}

}
