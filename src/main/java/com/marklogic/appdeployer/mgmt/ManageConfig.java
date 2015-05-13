package com.marklogic.appdeployer.mgmt;

import com.marklogic.appdeployer.util.RestConfig;

/**
 * Defines the configuration data for talking to the Mgmt REST API.
 */
public class ManageConfig extends RestConfig {

    public ManageConfig() {
        super("localhost", 8002, "admin", "admin");
    }

    public ManageConfig(String host, String password) {
        super(host, 8002, "admin", password);
    }

    public ManageConfig(String host, int port, String username, String password) {
        super(host, port, username, password);
    }
}
