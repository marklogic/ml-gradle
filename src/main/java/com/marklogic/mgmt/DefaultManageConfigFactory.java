package com.marklogic.mgmt;

import com.marklogic.mgmt.util.PropertySource;
import com.marklogic.mgmt.util.PropertySourceFactory;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class DefaultManageConfigFactory extends PropertySourceFactory implements ManageConfigFactory {

	private Map<String, BiConsumer<ManageConfig, String>> propertyConsumerMap;

    public DefaultManageConfigFactory() {
        super();
        initialize();
    }

    public DefaultManageConfigFactory(PropertySource propertySource) {
        super(propertySource);
        initialize();
    }

    public void initialize() {
	    // Order matters, so a LinkedHashMap is used to preserve the order
	    propertyConsumerMap = new LinkedHashMap<>();

	    propertyConsumerMap.put("mlManageHost", (config, prop) -> {
		    logger.info("Manage host: " + prop);
		    config.setHost(prop);
	    });

	    propertyConsumerMap.put("mlHost", (config, prop) -> {
	    	if (!propertyExists("mlManageHost")) {
			    logger.info("Manage host: " + prop);
			    config.setHost(prop);
		    }
	    });

	    propertyConsumerMap.put("mlManagePort", (config, prop) -> {
		    logger.info("Manage port: " + prop);
		    config.setPort(Integer.parseInt(prop));
	    });

	    propertyConsumerMap.put("mlManageUsername", (config, prop) -> {
		    logger.info("Manage username: " + prop);
		    config.setUsername(prop);
	    });

	    propertyConsumerMap.put("mlUsername", (config, prop) -> {
	    	if (!propertyExists("mlManageUsername")) {
			    logger.info("Manage username: " + prop);
			    config.setUsername(prop);
		    }
		    if (!propertyExists("mlSecurityUsername") && !propertyExists("mlAdminUsername")) {
			    logger.info("Manage user with security role: " + prop);
			    config.setSecurityUsername(prop);
		    }
	    });

	    propertyConsumerMap.put("mlManagePassword", (config, prop) -> {
		    config.setPassword(prop);
	    });

	    propertyConsumerMap.put("mlPassword", (config, prop) -> {
		    if (!propertyExists("mlManagePassword")) {
			    config.setPassword(prop);
		    }
		    if (!propertyExists("mlSecurityPassword") && !propertyExists("mlAdminPassword")) {
		    	config.setSecurityPassword(prop);
		    }
	    });

	    propertyConsumerMap.put("mlManageScheme", (config, prop) -> {
		    logger.info("Manage scheme: " + prop);
		    config.setScheme(prop);
	    });

	    propertyConsumerMap.put("mlManageSimpleSsl", (config, prop) -> {
		    logger.info("Use simple SSL for Manage app server: " + prop);
		    config.setConfigureSimpleSsl(Boolean.parseBoolean(prop));
	    });

	    propertyConsumerMap.put("mlManageCleanJsonPayloads", (config, prop) -> {
		    logger.info("Cleaning Management API JSON payloads: " + prop);
		    config.setCleanJsonPayloads(Boolean.parseBoolean(prop));
	    });

	    propertyConsumerMap.put("mlAdminUsername", (config, prop) -> {
		    logger.info("mlAdminUsername is deprecated; please use mlSecurityUsername instead; Manage user with security role: " + prop);
		    config.setSecurityUsername(prop);
	    });

	    propertyConsumerMap.put("mlAdminPassword", (config, prop) -> {
		    logger.info("mlAdminPassword is deprecated; please use mlSecurityPassword instead");
		    config.setSecurityPassword(prop);
	    });

	    propertyConsumerMap.put("mlSecurityUsername", (config, prop) -> {
		    logger.info("Manage user with security role: " + prop);
		    config.setSecurityUsername(prop);
	    });

	    propertyConsumerMap.put("mlSecurityPassword", (config, prop) -> {
		    config.setSecurityPassword(prop);
	    });
    }

    @Override
    public ManageConfig newManageConfig() {
        ManageConfig config = new ManageConfig();

	    for (String propertyName : propertyConsumerMap.keySet()) {
		    String value = getProperty(propertyName);
		    if (value != null) {
			    propertyConsumerMap.get(propertyName).accept(config, value);
		    }
	    }

	    if (!StringUtils.hasText(config.getSecurityUsername())) {
	    	config.setSecurityUsername(config.getUsername());
	    }
	    if (!StringUtils.hasText(config.getSecurityPassword())) {
	    	config.setSecurityPassword(config.getPassword());
	    }

        return config;
    }

	/**
	 * This is provided so that a client can easily print out a list of all the supported properties.
	 *
	 * @return
	 */
	public Map<String, BiConsumer<ManageConfig, String>> getPropertyConsumerMap() {
		return propertyConsumerMap;
	}
}
