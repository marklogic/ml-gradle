package com.marklogic.mgmt;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class ManageClientTest  {

	@Test
	public void determineUsernameForSecurityUserRequest() {
		ManageConfig config = new ManageConfig();
		config.setSecurityUsername("admin");
		config.setSecurityPassword("admin");
		config.setUsername("someone");

		ManageClient client = new ManageClient(config);
		assertEquals("admin", client.determineUsernameForSecurityUserRequest());

		config.setSecurityUsername(null);
		assertEquals("someone", client.determineUsernameForSecurityUserRequest());
	}
}
