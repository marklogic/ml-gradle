package com.marklogic.mgmt.util;

import java.util.Properties;

public class SimplePropertySource implements PropertySource {

    private Properties props;

    public SimplePropertySource(String... propNamesAndValues) {
        props = new Properties();
        for (int i = 0; i < propNamesAndValues.length; i += 2) {
            props.setProperty(propNamesAndValues[i], propNamesAndValues[i + 1]);
        }
    }

    public SimplePropertySource(Properties props) {
        this.props = props;
    }

    @Override
    public String getProperty(String name) {
        return props.getProperty(name);
    }

}
