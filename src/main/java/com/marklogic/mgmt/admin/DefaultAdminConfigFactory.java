package com.marklogic.mgmt.admin;

import com.marklogic.mgmt.util.PropertySource;
import com.marklogic.mgmt.util.PropertySourceFactory;

public class DefaultAdminConfigFactory extends PropertySourceFactory implements AdminConfigFactory {

    public DefaultAdminConfigFactory() {
        super();
    }

    public DefaultAdminConfigFactory(PropertySource propertySource) {
        super(propertySource);
    }

    @Override
    public AdminConfig newAdminConfig() {
        AdminConfig c = new AdminConfig();

        String mlUsername = getProperty("mlUsername");
        String mlPassword = getProperty("mlPassword");

        String prop = getProperty("mlAdminHost");
        if (prop != null) {
            logger.info("Admin host: " + prop);
            c.setHost(prop);
        } else {
            prop = getProperty("mlHost");
            if (prop != null) {
                logger.info("Admin host: " + prop);
                c.setHost(prop);
            }
        }

        prop = getProperty("mlAdminPort");
        if (prop != null) {
            logger.info("Admin port: " + prop);
            c.setPort(Integer.parseInt(prop));
        }

        prop = getProperty("mlAdminUsername");
        if (prop != null) {
            logger.info("Admin username: " + prop);
            c.setUsername(prop);
        } else if (mlUsername != null) {
            logger.info("Admin username: " + mlUsername);
            c.setUsername(mlUsername);
        }

        prop = getProperty("mlAdminPassword");
        if (prop != null) {
            c.setPassword(prop);
        } else if (mlPassword != null) {
            c.setPassword(mlPassword);
        }

        return c;
    }

}
