/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
		    config.setPort(Integer.parseInt(prop));
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
	    });

	    propertyConsumerMap.put("mlAdminTrustManagementAlgorithm", (config, prop) -> {
		    logger.info("Using trust management algorithm for SSL for Admin app server: " + prop);
		    config.setTrustManagementAlgorithm(prop);
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
