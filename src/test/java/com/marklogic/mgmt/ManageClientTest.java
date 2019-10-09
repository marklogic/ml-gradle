package com.marklogic.mgmt;

import org.junit.Assert;
import org.junit.Test;

public class ManageClientTest extends Assert {

	@Test
	public void determineUsernameForSecurityUserRequest() {
		ManageConfig config = new ManageConfig();
		config.setSecurityUsername("admin");
		config.setUsername("someone");

		ManageClient client = new ManageClient(config);
		assertEquals("admin", client.determineUsernameForSecurityUserRequest());

		config.setSecurityUsername(null);
		assertEquals("someone", client.determineUsernameForSecurityUserRequest());
	}
}
