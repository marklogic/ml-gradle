package com.rjrudin.marklogic.mgmt.util;

public class SystemPropertySource implements PropertySource {

    @Override
    public String getProperty(String name) {
        return System.getProperty(name);
    }

}
