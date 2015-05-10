package com.marklogic.appdeployer.mgmt;

import com.marklogic.appdeployer.util.RestConfig;

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
