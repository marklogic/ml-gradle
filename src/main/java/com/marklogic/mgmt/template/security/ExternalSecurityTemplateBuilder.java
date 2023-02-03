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
