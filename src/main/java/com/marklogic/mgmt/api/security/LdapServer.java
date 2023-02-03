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
package com.marklogic.mgmt.api.security;

public class LdapServer {

	private String ldapServerUri;
	private String ldapBase;
	private String ldapAttribute;
	private String ldapDefaultUser;
	private String ldapPassword;
	private String ldapBindMethod;

	public String getLdapServerUri() {
		return ldapServerUri;
	}

	public void setLdapServerUri(String ldapServerUri) {
		this.ldapServerUri = ldapServerUri;
	}

	public String getLdapBase() {
		return ldapBase;
	}

	public void setLdapBase(String ldapBase) {
		this.ldapBase = ldapBase;
	}

	public String getLdapAttribute() {
		return ldapAttribute;
	}

	public void setLdapAttribute(String ldapAttribute) {
		this.ldapAttribute = ldapAttribute;
	}

	public String getLdapDefaultUser() {
		return ldapDefaultUser;
	}

	public void setLdapDefaultUser(String ldapDefaultUser) {
		this.ldapDefaultUser = ldapDefaultUser;
	}

	public String getLdapPassword() {
		return ldapPassword;
	}

	public void setLdapPassword(String ldapPassword) {
		this.ldapPassword = ldapPassword;
	}

	public String getLdapBindMethod() {
		return ldapBindMethod;
	}

	public void setLdapBindMethod(String ldapBindMethod) {
		this.ldapBindMethod = ldapBindMethod;
	}
}
