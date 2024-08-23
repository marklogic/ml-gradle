package com.marklogic.mgmt.admin;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AdminManagerTest {

	@Test
	void invalidConfig() {
		// As of 4.5.1, this should no longer throw an error as the connection to MarkLogic should
		// be lazily constructed.
		AdminManager mgr = new AdminManager(new AdminConfig("localhost", 8001, null, null));

		RuntimeException ex = assertThrows(RuntimeException.class, () -> mgr.getRestTemplate());
		assertTrue(ex.getMessage().contains("username must be of type String"),
			"The call to get a RestTemplate is expected to fail because no username/password has been " +
				"provided, and digest auth is used by default. Prior to 4.5.1, this failed when the AdminManager " +
				"was instantiated, which proved problematic in 4.5.0 when the user tried to run an ml-gradle task " +
				"without providing any credentials.");
	}
}
