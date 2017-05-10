package com.marklogic.mgmt;

import com.marklogic.mgmt.util.PropertySource;
import com.marklogic.mgmt.util.PropertySourceFactory;

public class DefaultManageConfigFactory extends PropertySourceFactory implements ManageConfigFactory {

    public DefaultManageConfigFactory() {
        super();
    }

    public DefaultManageConfigFactory(PropertySource propertySource) {
        super(propertySource);
    }

    @Override
    public ManageConfig newManageConfig() {
        ManageConfig c = new ManageConfig();

        String mlUsername = getProperty("mlUsername");
        String mlPassword = getProperty("mlPassword");

        String prop = getProperty("mlManageHost");
        if (prop != null) {
            logger.info("Manage host: " + prop);
            c.setHost(prop);
        } else {
            prop = getProperty("mlHost");
            if (prop != null) {
                logger.info("Manage host: " + prop);
                c.setHost(prop);
            }
        }

        prop = getProperty("mlManagePort");
        if (prop != null) {
            logger.info("Manage port: " + prop);
            c.setPort(Integer.parseInt(prop));
        }

        prop = getProperty("mlManageUsername");
        if (prop != null) {
            logger.info("Manage username: " + prop);
            c.setUsername(prop);
        } else if (mlUsername != null) {
            logger.info("Manage username: " + mlUsername);
            c.setUsername(mlUsername);
        }

        prop = getProperty("mlManagePassword");
        if (prop != null) {
            c.setPassword(prop);
        } else if (mlPassword != null) {
            c.setPassword(mlPassword);
        }

	    prop = getProperty("mlManageScheme");
	    if (prop != null) {
		    logger.info("Manage scheme: " + prop);
		    c.setScheme(prop);
	    }

	    prop = getProperty("mlAdminUsername");
        if (prop != null) {
            logger.info("Manage admin username: " + prop);
            c.setAdminUsername(prop);
        } else if (mlUsername != null) {
            logger.info("Manage admin username: " + mlUsername);
            c.setAdminUsername(mlUsername);
        } else {
            c.setAdminUsername(c.getUsername());
        }

        prop = getProperty("mlAdminPassword");
        if (prop != null) {
            c.setAdminPassword(prop);
        } else if (mlPassword != null) {
            c.setAdminPassword(mlPassword);
        } else {
            c.setAdminPassword(c.getPassword());
        }

        return c;
    }

}
