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
