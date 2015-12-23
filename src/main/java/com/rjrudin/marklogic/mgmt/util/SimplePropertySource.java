package com.rjrudin.marklogic.mgmt.util;

import java.util.Properties;

public class SimplePropertySource implements PropertySource {

    private Properties props;

    public SimplePropertySource(String... propNamesAndValues) {
        props = new Properties();
        for (int i = 0; i < propNamesAndValues.length; i += 2) {
            props.setProperty(propNamesAndValues[i], propNamesAndValues[i + 1]);
        }
    }

    @Override
    public String getProperty(String name) {
        return props.getProperty(name);
    }

}
