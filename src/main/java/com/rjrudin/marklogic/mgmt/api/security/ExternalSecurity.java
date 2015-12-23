package com.rjrudin.marklogic.mgmt.api.security;

import com.rjrudin.marklogic.mgmt.ResourceManager;
import com.rjrudin.marklogic.mgmt.api.API;
import com.rjrudin.marklogic.mgmt.api.Resource;
import com.rjrudin.marklogic.mgmt.security.ExternalSecurityManager;

public class ExternalSecurity extends Resource {

    private String externalSecurityName;
    private String description;
    private String authentication;
    private Integer cacheTimeout;
    private String authorization;
    private String ldapServerUri;
    private String ldapBase;
    private String ldapAttribute;
    private String ldapDefaultUser;
    private String ldapPassword;
    private String ldapBindMethod;

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
