package com.marklogic.appdeployer.command.servers;

import com.marklogic.mgmt.resource.appservers.ServerManager;
import org.junit.Assert;
import org.junit.Test;

public class DeployServerWithExternalSecurityTest extends Assert {

	@Test
	public void test() {
		ServerManager mgr = new ServerManager(null);

		assertTrue(mgr.useSecurityUser("{\"server-name\": \"my-server\", \"external-security\": [\"my-external-security\"]}"));
		assertFalse(mgr.useSecurityUser("{\"server-name\": \"my-server\"}"));

		assertTrue(
			"This is an expected false positive, but it's considered fine because it just means that the security user " +
				"will be used in the rare event that some other field in the payload has the string 'external-security' in it",
			mgr.useSecurityUser("{\"server-name\": \"my-external-security-test\"}")
		);
	}
}
