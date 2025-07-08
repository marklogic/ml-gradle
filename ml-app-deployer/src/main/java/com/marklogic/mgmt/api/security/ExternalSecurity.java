/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt.api.security;

import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.api.API;
import com.marklogic.mgmt.api.Resource;
import com.marklogic.mgmt.resource.security.ExternalSecurityManager;

public class ExternalSecurity extends Resource {

    private String externalSecurityName;
    private String description;
    private String authentication;
    private Integer cacheTimeout;
    private String authorization;
    private LdapServer ldapServer;

    public ExternalSecurity() {
        super();
    }

    public ExternalSecurity(API api, String name) {
        super(api);
        this.externalSecurityName = name;
    }

    @Override
    protected ResourceManager getResourceManager() {
        return new ExternalSecurityManager(getClient());
    }

    @Override
    protected String getResourceId() {
        return externalSecurityName;
    }

    public String getExternalSecurityName() {
        return externalSecurityName;
    }

    public void setExternalSecurityName(String externalSecurityName) {
        this.externalSecurityName = externalSecurityName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthentication() {
        return authentication;
    }

    public void setAuthentication(String authentication) {
        this.authentication = authentication;
    }

    public Integer getCacheTimeout() {
        return cacheTimeout;
    }

    public void setCacheTimeout(Integer cacheTimeout) {
        this.cacheTimeout = cacheTimeout;
    }

    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

	public LdapServer getLdapServer() {
		return ldapServer;
	}

	public void setLdapServer(LdapServer ldapServer) {
		this.ldapServer = ldapServer;
	}
}
