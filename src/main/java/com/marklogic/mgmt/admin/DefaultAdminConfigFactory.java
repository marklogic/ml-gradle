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
            logger.info("Admin interface host: " + prop);
            c.setHost(prop);
        } else {
            prop = getProperty("mlHost");
            if (prop != null) {
                logger.info("Admin interface host: " + prop);
                c.setHost(prop);
            }
        }

        prop = getProperty("mlAdminPort");
        if (prop != null) {
            logger.info("Admin interface port: " + prop);
            c.setPort(Integer.parseInt(prop));
        }

	    /**
	     * The Manage API endpoints in the Admin interface still just require the manage-admin role, so the value of
	     * mlManageUsername should work for these calls.
	     */
	    prop = getProperty("mlManageUsername");
        if (prop != null) {
            logger.info("Admin interface username: " + prop);
            c.setUsername(prop);
        } else if (mlUsername != null) {
            logger.info("Admin interface username: " + mlUsername);
            c.setUsername(mlUsername);
        }

        prop = getProperty("mlManagePassword");
        if (prop != null) {
            c.setPassword(prop);
        } else if (mlPassword != null) {
            c.setPassword(mlPassword);
        }

	    prop = getProperty("mlAdminScheme");
	    if (prop != null) {
		    logger.info("Admin interface scheme: " + prop);
		    c.setScheme(prop);
	    }

	    prop = getProperty("mlAdminSimpleSsl");
	    if (prop != null) {
	    	logger.info("Use simple SSL for Admin interface: " + prop);
	    	c.setConfigureSimpleSsl(Boolean.parseBoolean(prop));
	    }

        return c;
    }

}
