package com.marklogic.appdeployer.command.servers;

import com.marklogic.mgmt.resource.appservers.ServerManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DeployServerWithExternalSecurityTest {

	@Test
	public void test() {
		ServerManager mgr = new ServerManager(null);

		assertTrue(mgr.useSecurityUser("{\"server-name\": \"my-server\", \"external-security\": [\"my-external-security\"]}"));
		assertFalse(mgr.useSecurityUser("{\"server-name\": \"my-server\"}"));

		assertTrue(
			mgr.useSecurityUser("{\"server-name\": \"my-external-security-test\"}"),
			"This is an expected false positive, but it's considered fine because it just means that the security user " +
				"will be used in the rare event that some other field in the payload has the string 'external-security' in it"
		);
	}
}
