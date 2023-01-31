package com.marklogic.mgmt;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class ManageClientTest  {

	@Test
	public void determineUsernameForSecurityUserRequest() {
		ManageConfig config = new ManageConfig("localhost", 8002, "someone", "someword");
		config.setSecurityUsername("admin");
		config.setSecurityPassword("admin");

		ManageClient client = new ManageClient(config);
		assertEquals("admin", client.determineUsernameForSecurityUserRequest());

		config.setSecurityUsername(null);
		assertEquals("someone", client.determineUsernameForSecurityUserRequest());
	}
}
