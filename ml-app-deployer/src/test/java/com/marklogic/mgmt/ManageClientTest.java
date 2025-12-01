/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt;

import com.marklogic.mgmt.resource.databases.DatabaseManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ManageClientTest {

	@Test
	void determineUsernameForSecurityUserRequest() {
		ManageConfig config = new ManageConfig("localhost", 8002, "someone", "someword");
		config.setSecurityUsername("admin");
		config.setSecurityPassword("admin");

		ManageClient client = new ManageClient(config);
		assertEquals("admin", client.determineUsernameForSecurityUserRequest());

		config.setSecurityUsername(null);
		assertEquals("someone", client.determineUsernameForSecurityUserRequest());
	}

	@Test
	void nullUsername() {
		// Will be the case when doing cloud auth.
		ManageConfig config = new ManageConfig("localhost", 8002, null, null);
		ManageClient client = new ManageClient(config);
		assertEquals("", client.determineUsernameForSecurityUserRequest());
	}

	@Test
	void nullManageConfig() {
		ManageClient client = new ManageClient((ManageConfig) null);
		NullPointerException npe = assertThrows(NullPointerException.class, () -> new DatabaseManager(client).getAsXml());
		assertEquals("A ManageConfig instance must be provided", npe.getMessage(),
			"It's possible to pass in null as the ManageConfig since there's still a setManageConfig method, but that's been " +
				"deprecated so that it can be removed in 7.0.0. The goal is to have ManageConfig be final once " +
				"it's set, and ideally hidden as well so that the ManageClient is effectively immutable. " +
				"In the meantime, we expect a nice error message if the ManageConfig is null.");
	}
}
