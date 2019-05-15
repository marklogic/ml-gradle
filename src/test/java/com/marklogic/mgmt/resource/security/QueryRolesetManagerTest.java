package com.marklogic.mgmt.resource.security;

import com.marklogic.mgmt.AbstractMgmtTest;
import org.junit.Test;

public class QueryRolesetManagerTest extends AbstractMgmtTest {

	@Test
	public void checkForRolesetThatDoesntExist() {
		QueryRolesetManager mgr = new QueryRolesetManager(manageClient);
		assertFalse("Smoke test that this method won't submit a string to the Manage API, which will cause " +
			"a 500 error because it can't be cast to an unsignedLong", mgr.exists("[\"doesnt-exist\"]"));
	}
}
