package com.marklogic.rest.mgmt;

import com.marklogic.rest.util.RestConfig;

/**
 * Defines the configuration data for talking to the Mgmt REST API. Also includes properties for the admin user, as this
 * user is typically needed for managing resources such as roles and users. If adminUsername and adminPassword are not
 * set, they default to the username/password attribute values.
 */
public class ManageConfig extends RestConfig {

    private String adminUsername;
    private String adminPassword;

    public ManageConfig() {
        this("localhost", "admin");
    }

    public ManageConfig(String host, String password) {
        this(host, 8002, "admin", password);
    }

    public ManageConfig(String host, int port, String username, String password) {
        super(host, port, username, password);
        setAdminUsername(username);
        setAdminPassword(password);
    }

    public String getAdminUsername() {
        return adminUsername;
    }

    public void setAdminUsername(String adminUsername) {
        this.adminUsername = adminUsername;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }
}
