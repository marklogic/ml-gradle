/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.scaffold;

import com.marklogic.appdeployer.command.security.DeployExternalSecurityCommand;
import com.marklogic.mgmt.api.security.ExternalSecurity;
import com.marklogic.mgmt.api.security.LdapServer;
import com.marklogic.mgmt.template.security.ExternalSecurityTemplateBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WriteExternalSecurityTest extends AbstractResourceWriterTest {

	@Test
	void defaultValues() {
		initializeAppDeployer(new DeployExternalSecurityCommand());
		buildResourceAndDeploy(new ExternalSecurityTemplateBuilder());

		ExternalSecurity es = api.externalSecurity("CHANGEME-name");
		System.out.println(es.getJson());
		assertEquals("CHANGEME description of external security", es.getDescription());
		assertEquals("ldap", es.getAuthentication());
		assertEquals(300, es.getCacheTimeout());
		assertEquals("internal", es.getAuthorization());

		LdapServer ls = es.getLdapServer();
		assertEquals("ldap://CHANGEME:389", ls.getLdapServerUri());
		assertEquals("CHANGEME", ls.getLdapBase());
		assertEquals("CHANGEME", ls.getLdapAttribute());
		assertEquals("CHANGEME", ls.getLdapDefaultUser());
	}
}
