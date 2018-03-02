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
