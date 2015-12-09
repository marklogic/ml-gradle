package com.rjrudin.marklogic.mgmt;

import com.rjrudin.marklogic.rest.util.RestConfig;

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

    /**
     * Convenience method for building a new instance from the following system properties: mlManageHost, mlManagePort,
     * mlManageUsername, mlManagePassword, mlAdminUsername, and mlAdminPassword. First created to assist with groovysh
     * integration.
     * 
     * @return
     */
    public static ManageConfig buildFromSystemProps() {
        ManageConfig c = new ManageConfig();
        String prop = System.getProperty("mlManageHost");
        if (prop != null) {
            c.setHost(prop);
        }
        prop = System.getProperty("mlManagePort");
        if (prop != null) {
            c.setPort(Integer.parseInt(prop));
        }
        prop = System.getProperty("mlManageUsername");
        if (prop != null) {
            c.setUsername(prop);
        }
        prop = System.getProperty("mlManagePassword");
        if (prop != null) {
            c.setPassword(prop);
        }
        prop = System.getProperty("mlAdminUsername");
        if (prop != null) {
            c.setAdminUsername(prop);
        }
        prop = System.getProperty("mlAdminPassword");
        if (prop != null) {
            c.setAdminPassword(prop);
        }
        return c;
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
