package com.marklogic.appdeployer.mgmt;

import com.marklogic.appdeployer.util.RestConfig;

/**
 * For un-installing an application, we need to know how to invoke the host:8000/v1/eval endpoint - this class defines
 * that configuration.
 */
public class AppServicesConfig extends RestConfig {

    public AppServicesConfig() {
        super("localhost", 8000, "admin", "admin");
    }

    public AppServicesConfig(String host, String password) {
        super(host, 8000, "admin", password);
    }

    public AppServicesConfig(String host, int port, String username, String password) {
        super(host, port, username, password);
    }
}
