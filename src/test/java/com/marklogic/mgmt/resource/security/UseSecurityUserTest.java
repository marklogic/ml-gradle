package com.marklogic.mgmt.resource.security;

import org.junit.Assert;
import org.junit.Test;

public class UseSecurityUserTest extends Assert {

	/**
	 * Just a little unit test to make sure these manager classes all use the security user.
	 */
	@Test
	public void test() {
		assertTrue(new AmpManager(null).useSecurityUser());
		assertTrue(new CertificateAuthorityManager(null).useSecurityUser());
		assertTrue(new CertificateTemplateManager(null).useSecurityUser());
		assertTrue(new ExternalSecurityManager(null).useSecurityUser());
		assertTrue(new PrivilegeManager(null).useSecurityUser());
		assertTrue(new ProtectedCollectionsManager(null).useSecurityUser());
		assertTrue(new ProtectedPathManager(null).useSecurityUser());
		assertTrue(new QueryRolesetsManager(null).useSecurityUser());
		assertTrue(new RoleManager(null).useSecurityUser());
		assertTrue(new UserManager(null).useSecurityUser());
	}

	@Test
	public void genericResourceManagerDoesNotUseSecurityUser() {
		assertFalse(new TestManager().useSecurityUser());
	}
}
