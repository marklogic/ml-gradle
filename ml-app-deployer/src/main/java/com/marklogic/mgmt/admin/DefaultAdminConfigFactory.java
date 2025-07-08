/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt.admin;

import com.marklogic.appdeployer.util.JavaClientUtil;
import com.marklogic.mgmt.util.PropertySource;
import com.marklogic.mgmt.util.PropertySourceFactory;
import org.springframework.util.StringUtils;

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
		    config.setPort(propertyToInteger("mlAdminPort", prop));
	    });

		propertyConsumerMap.put("mlAdminAuthentication", (config, prop) -> {
			logger.info("Admin authentication: " + prop);
			config.setAuthType(prop);
		});

		propertyConsumerMap.put("mlAuthentication", (config, prop) -> {
			if (!propertyExists("mlAdminAuthentication")) {
				logger.info("Admin authentication: " + prop);
				config.setAuthType(prop);
			}
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

		propertyConsumerMap.put("mlAdminCertFile", (config, prop) -> {
			logger.info("Admin certificate file: " + prop);
			config.setCertFile(prop);
		});
		propertyConsumerMap.put("mlAdminCertPassword", (config, prop) -> {
			config.setCertPassword(prop);
		});
		propertyConsumerMap.put("mlAdminExternalName", (config, prop) -> {
			logger.info("Admin external name: " + prop);
			config.setExternalName(prop);
		});
		propertyConsumerMap.put("mlAdminSamlToken", (config, prop) -> {
			config.setSamlToken(prop);
		});
		propertyConsumerMap.put("mlAdminOauthToken", (config, prop) -> {
			config.setOauthToken(prop);
		});

		propertyConsumerMap.put("mlCloudBasePath", (config, prop) -> {
			String defaultAdminPath = prop + "/admin";
			logger.info("Admin base path: " + defaultAdminPath);
			config.setBasePath(defaultAdminPath);
		});
		propertyConsumerMap.put("mlAdminBasePath", (config, prop) -> {
			String cloudBasePath = getProperty("mlCloudBasePath");
			String adminPath = StringUtils.hasText(cloudBasePath) ? cloudBasePath + prop : prop;
			logger.info("Admin base path: " + adminPath);
			config.setBasePath(adminPath);
		});

	    propertyConsumerMap.put("mlAdminSimpleSsl", (config, prop) -> {
		    logger.info("Use simple SSL for Admin interface: " + prop);
		    config.setConfigureSimpleSsl(Boolean.parseBoolean(prop));
			config.setScheme("https");
	    });

	    propertyConsumerMap.put("mlAdminSslProtocol", (config, prop) -> {
		    logger.info("Using SSL protocol for Admin app server: " + prop);
		    config.setSslProtocol(prop);
			config.setScheme("https");
	    });

		propertyConsumerMap.put("mlAdminSslHostnameVerifier", (config, prop) -> {
			logger.info("Admin SSL hostname verifier: " + prop);
			config.setSslHostnameVerifier(JavaClientUtil.toSSLHostnameVerifier(prop));
		});
		propertyConsumerMap.put("mlSslHostnameVerifier", (config, prop) -> {
			if (!propertyExists("mlAdminSslHostnameVerifier")) {
				logger.info("Admin SSL hostname verifier: " + prop);
				config.setSslHostnameVerifier(JavaClientUtil.toSSLHostnameVerifier(prop));
			}
		});

	    propertyConsumerMap.put("mlAdminUseDefaultKeystore", (config, prop) -> {
		    logger.info("Using default JVM keystore for SSL for Admin app server: " + prop);
		    config.setUseDefaultKeystore(Boolean.parseBoolean(prop));
			config.setScheme("https");
	    });

	    propertyConsumerMap.put("mlAdminTrustManagementAlgorithm", (config, prop) -> {
		    logger.info("Using trust management algorithm for SSL for Admin app server: " + prop);
		    config.setTrustManagementAlgorithm(prop);
	    });

		propertyConsumerMap.put("mlKeyStorePath", (config, prop) -> {
			logger.info("Admin key store path: " + prop);
			config.setKeyStorePath(prop);
			config.setScheme("https");
		});
		propertyConsumerMap.put("mlKeyStorePassword", (config, prop) -> {
			config.setKeyStorePassword(prop);
		});
		propertyConsumerMap.put("mlKeyStoreType", (config, prop) -> {
			logger.info("Admin key store type: " + prop);
			config.setKeyStoreType(prop);
		});
		propertyConsumerMap.put("mlKeyStoreAlgorithm", (config, prop) -> {
			logger.info("Admin key store algorithm: " + prop);
			config.setKeyStoreAlgorithm(prop);
		});
		propertyConsumerMap.put("mlTrustStorePath", (config, prop) -> {
			logger.info("Admin trust store path: " + prop);
			config.setTrustStorePath(prop);
			config.setScheme("https");
		});
		propertyConsumerMap.put("mlTrustStorePassword", (config, prop) -> {
			config.setTrustStorePassword(prop);
		});
		propertyConsumerMap.put("mlTrustStoreType", (config, prop) -> {
			logger.info("Admin trust store type: " + prop);
			config.setTrustStoreType(prop);
		});
		propertyConsumerMap.put("mlTrustStoreAlgorithm", (config, prop) -> {
			logger.info("Admin trust store algorithm: " + prop);
			config.setTrustStoreAlgorithm(prop);
		});

		propertyConsumerMap.put("mlAdminKeyStorePath", (config, prop) -> {
			logger.info("Admin key store path: " + prop);
			config.setKeyStorePath(prop);
			config.setScheme("https");
		});
		propertyConsumerMap.put("mlAdminKeyStorePassword", (config, prop) -> {
			config.setKeyStorePassword(prop);
		});
		propertyConsumerMap.put("mlAdminKeyStoreType", (config, prop) -> {
			logger.info("Admin key store type: " + prop);
			config.setKeyStoreType(prop);
		});
		propertyConsumerMap.put("mlAdminKeyStoreAlgorithm", (config, prop) -> {
			logger.info("Admin key store algorithm: " + prop);
			config.setKeyStoreAlgorithm(prop);
		});
		propertyConsumerMap.put("mlAdminTrustStorePath", (config, prop) -> {
			logger.info("Admin trust store path: " + prop);
			config.setTrustStorePath(prop);
			config.setScheme("https");
		});
		propertyConsumerMap.put("mlAdminTrustStorePassword", (config, prop) -> {
			config.setTrustStorePassword(prop);
		});
		propertyConsumerMap.put("mlAdminTrustStoreType", (config, prop) -> {
			logger.info("Admin trust store type: " + prop);
			config.setTrustStoreType(prop);
		});
		propertyConsumerMap.put("mlAdminTrustStoreAlgorithm", (config, prop) -> {
			logger.info("Admin trust store algorithm: " + prop);
			config.setTrustStoreAlgorithm(prop);
		});

		propertyConsumerMap.put("mlAdminScheme", (config, prop) -> {
			logger.info("Admin scheme: " + prop);
			config.setScheme(prop);
		});

		// Processed last so that it can override scheme/port
		propertyConsumerMap.put("mlCloudApiKey", (config, prop) -> {
			logger.info("Setting Admin cloud API key and forcing scheme to HTTPS and port to 443");
			config.setCloudApiKey(prop);
			config.setPort(443);
			config.setScheme("https");
		});
	}

    @Override
    public AdminConfig newAdminConfig() {
        AdminConfig config = new AdminConfig();

	    for (String propertyName : propertyConsumerMap.keySet()) {
		    String value = getProperty(propertyName);
		    if (StringUtils.hasText(value)) {
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
