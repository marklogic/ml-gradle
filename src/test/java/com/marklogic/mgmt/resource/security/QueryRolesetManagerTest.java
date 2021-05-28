package com.marklogic.mgmt.resource.security;

import com.marklogic.mgmt.AbstractMgmtTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class QueryRolesetManagerTest extends AbstractMgmtTest {

	@Test
	public void checkForRolesetThatDoesntExist() {
		QueryRolesetManager mgr = new QueryRolesetManager(manageClient);
		assertFalse(mgr.exists("[\"doesnt-exist\"]"), "Smoke test that this method won't submit a string to the Manage API, which will cause " +
			"a 500 error because it can't be cast to an unsignedLong");
	}
}
