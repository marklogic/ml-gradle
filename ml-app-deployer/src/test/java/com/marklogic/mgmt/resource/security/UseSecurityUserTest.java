/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.mgmt.resource.security;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class UseSecurityUserTest  {

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
		assertTrue(new QueryRolesetManager(null).useSecurityUser());
		assertTrue(new RoleManager(null).useSecurityUser());
		assertTrue(new UserManager(null).useSecurityUser());
	}

	@Test
	public void genericResourceManagerDoesNotUseSecurityUser() {
		assertFalse(new TestManager().useSecurityUser());
	}
}
