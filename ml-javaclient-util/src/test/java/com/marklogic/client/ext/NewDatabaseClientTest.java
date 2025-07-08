/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.ext;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NewDatabaseClientTest {

	private DatabaseClientConfig config;

	@BeforeEach
	void setup() {
		config = new DatabaseClientConfig("localhost", 8028);
	}

	@Test
	void saml() {
		config.setSecurityContextType(SecurityContextType.SAML);
		config.setSamlToken("my-token");

		DatabaseClient client = new DefaultConfiguredDatabaseClientFactory().newDatabaseClient(config);
		DatabaseClientFactory.SecurityContext context = client.getSecurityContext();

		assertTrue(context instanceof DatabaseClientFactory.SAMLAuthContext);
		DatabaseClientFactory.SAMLAuthContext samlContext = (DatabaseClientFactory.SAMLAuthContext) context;
		assertEquals("my-token", samlContext.getToken());
	}
}
