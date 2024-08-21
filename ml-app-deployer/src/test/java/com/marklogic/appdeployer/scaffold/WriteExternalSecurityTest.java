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
package com.marklogic.appdeployer.scaffold;

import com.marklogic.appdeployer.command.security.DeployExternalSecurityCommand;
import com.marklogic.mgmt.api.security.ExternalSecurity;
import com.marklogic.mgmt.api.security.LdapServer;
import com.marklogic.mgmt.template.security.ExternalSecurityTemplateBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WriteExternalSecurityTest extends AbstractResourceWriterTest {

	@Test
	public void defaultValues() {
		initializeAppDeployer(new DeployExternalSecurityCommand());
		buildResourceAndDeploy(new ExternalSecurityTemplateBuilder());

		ExternalSecurity es = api.externalSecurity("CHANGEME-name");
		System.out.println(es.getJson());
		assertEquals("CHANGEME description of external security", es.getDescription());
		assertEquals("ldap", es.getAuthentication());
		assertEquals(new Integer(300), es.getCacheTimeout());
		assertEquals("internal", es.getAuthorization());

		LdapServer ls = es.getLdapServer();
		assertEquals("ldap://CHANGEME:389", ls.getLdapServerUri());
		assertEquals("CHANGEME", ls.getLdapBase());
		assertEquals("CHANGEME", ls.getLdapAttribute());
		assertEquals("CHANGEME", ls.getLdapDefaultUser());
		assertEquals("MD5", ls.getLdapBindMethod());
	}
}
