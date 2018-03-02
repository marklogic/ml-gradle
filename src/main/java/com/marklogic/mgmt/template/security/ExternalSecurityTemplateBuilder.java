package com.marklogic.mgmt.template.security;

import com.marklogic.mgmt.api.security.ExternalSecurity;
import com.marklogic.mgmt.api.security.LdapServer;
import com.marklogic.mgmt.template.GenericTemplateBuilder;

public class ExternalSecurityTemplateBuilder extends GenericTemplateBuilder {

	public ExternalSecurityTemplateBuilder() {
		super(ExternalSecurity.class);
		addDefaultPropertyValue("external-security-name", "CHANGEME-name");
		addDefaultPropertyValue("description", "CHANGEME description of external security");
		addDefaultPropertyValue("authentication", "ldap");
		addDefaultPropertyValue("cache-timeout", "300");
		addDefaultPropertyValue("authorization", "internal");

		LdapServer ls = new LdapServer();
		ls.setLdapAttribute("CHANGEME");
		ls.setLdapBase("CHANGEME");
		ls.setLdapDefaultUser("CHANGEME");
		ls.setLdapPassword("CHANGEME");
		ls.setLdapServerUri("ldap://CHANGEME:389");
		ls.setLdapBindMethod("MD5");
		addDefaultPropertyValue("ldap-server", ls);
	}
}
