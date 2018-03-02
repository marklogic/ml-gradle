package com.marklogic.appdeployer.scaffold;

import com.marklogic.appdeployer.command.security.DeployExternalSecurityCommand;
import com.marklogic.mgmt.api.security.ExternalSecurity;
import com.marklogic.mgmt.api.security.LdapServer;
import com.marklogic.mgmt.template.security.ExternalSecurityTemplateBuilder;
import org.junit.Test;

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
