package com.marklogic.mgmt.admin;

import com.marklogic.mgmt.util.PropertySource;
import com.marklogic.mgmt.util.PropertySourceFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class DefaultAdminConfigFactory extends PropertySourceFactory implements AdminConfigFactory {

	private Map<String, BiConsumer<AdminConfig, String>> propertyConsumerMap;

    public DefaultAdminConfigFactory() {
        super();
        initialize();
    }

    public DefaultAdminConfigFactory(PropertySource propertySource) {
        super(propertySource);
        initialize();
    }

    public void initialize() {
	    // Order matters, so a LinkedHashMap is used to preserve the order
	    propertyConsumerMap = new LinkedHashMap<>();

	    propertyConsumerMap.put("mlAdminHost", (config, prop) -> {
		    logger.info("Admin interface host: " + prop);
		    config.setHost(prop);
	    });

	    propertyConsumerMap.put("mlHost", (config, prop) -> {
		    if (!propertyExists("mlAdminHost")) {
			    logger.info("Admin interface host: " + prop);
			    config.setHost(prop);
		    }
	    });

	    propertyConsumerMap.put("mlAdminPort", (config, prop) -> {
		    logger.info("Admin interface port: " + prop);
		    config.setPort(Integer.parseInt(prop));
	    });

	    /**
	     * The Manage API endpoints in the Admin interface still just require the manage-admin role, so the value of
	     * mlManageUsername should work for these calls.
	     */
	    propertyConsumerMap.put("mlManageUsername", (config, prop) -> {
		    logger.info("Admin interface username: " + prop);
		    config.setUsername(prop);
	    });

	    propertyConsumerMap.put("mlUsername", (config, prop) -> {
		    if (!propertyExists("mlManageUsername")) {
			    logger.info("Admin interface username: " + prop);
			    config.setUsername(prop);
		    }
	    });

	    propertyConsumerMap.put("mlManagePassword", (config, prop) -> {
		    config.setPassword(prop);
	    });

	    propertyConsumerMap.put("mlPassword", (config, prop) -> {
		    if (!propertyExists("mlManagePassword")) {
			    config.setPassword(prop);
		    }
	    });

	    propertyConsumerMap.put("mlAdminScheme", (config, prop) -> {
		    logger.info("Admin interface scheme: " + prop);
		    config.setScheme(prop);
	    });

	    propertyConsumerMap.put("mlAdminSimpleSsl", (config, prop) -> {
		    logger.info("Use simple SSL for Admin interface: " + prop);
		    config.setConfigureSimpleSsl(Boolean.parseBoolean(prop));
	    });

	    propertyConsumerMap.put("mlAdminSslProtocol", (config, prop) -> {
		    logger.info("Using SSL protocol for Admin app server: " + prop);
		    config.setSslProtocol(prop);
	    });

	    propertyConsumerMap.put("mlAdminUseDefaultKeystore", (config, prop) -> {
		    logger.info("Using default JVM keystore for SSL for Admin app server: " + prop);
		    config.setUseDefaultKeystore(Boolean.parseBoolean(prop));
	    });

	    propertyConsumerMap.put("mlAdminTrustManagementAlgorithm", (config, prop) -> {
		    logger.info("Using trust management algorithm for SSL for Admin app server: " + prop);
		    config.setTrustManagementAlgorithm(prop);
	    });
    }

    @Override
    public AdminConfig newAdminConfig() {
        AdminConfig config = new AdminConfig();

	    for (String propertyName : propertyConsumerMap.keySet()) {
		    String value = getProperty(propertyName);
		    if (value != null) {
			    propertyConsumerMap.get(propertyName).accept(config, value);
		    }
	    }

        return config;
    }

	/**
	 * This is provided so that a client can easily print out a list of all the supported properties.
	 *
	 * @return
	 */
	public Map<String, BiConsumer<AdminConfig, String>> getPropertyConsumerMap() {
		return propertyConsumerMap;
	}
}
