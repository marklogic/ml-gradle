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
package com.marklogic.mgmt;

import com.marklogic.appdeployer.util.JavaClientUtil;
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

		propertyConsumerMap.put("mlManageAuthentication", (config, prop) -> {
			logger.info("Manage authentication: " + prop);
			config.setAuthType(prop);
		});

		propertyConsumerMap.put("mlAuthentication", (config, prop) -> {
			if (!propertyExists("mlManageAuthentication")) {
				logger.info("Manage authentication: " + prop);
				config.setAuthType(prop);
			}
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

		propertyConsumerMap.put("mlManageCertFile", (config, prop) -> {
			logger.info("Manage certificate file: " + prop);
			config.setCertFile(prop);
		});
		propertyConsumerMap.put("mlManageCertPassword", (config, prop) -> {
			config.setCertPassword(prop);
		});
		propertyConsumerMap.put("mlManageExternalName", (config, prop) -> {
			logger.info("Manage external name: " + prop);
			config.setExternalName(prop);
		});
		propertyConsumerMap.put("mlManageSamlToken", (config, prop) -> {
			config.setSamlToken(prop);
		});

		propertyConsumerMap.put("mlCloudBasePath", (config, prop) -> {
			String defaultManagePath = prop + "/manage";
			logger.info("Manage base path: " + defaultManagePath);
			config.setBasePath(defaultManagePath);
		});
		propertyConsumerMap.put("mlManageBasePath", (config, prop) -> {
			String cloudBasePath = getProperty("mlCloudBasePath");
			String managePath = StringUtils.hasText(cloudBasePath) ? cloudBasePath + prop : prop;
			logger.info("Manage base path: " + managePath);
			config.setBasePath(managePath);
		});

	    propertyConsumerMap.put("mlManageScheme", (config, prop) -> {
		    logger.info("Manage scheme: " + prop);
		    config.setScheme(prop);
	    });

	    propertyConsumerMap.put("mlManageSimpleSsl", (config, prop) -> {
		    logger.info("Use simple SSL for Manage app server: " + prop);
		    config.setConfigureSimpleSsl(Boolean.parseBoolean(prop));
	    });

	    propertyConsumerMap.put("mlManageSslProtocol", (config, prop) -> {
		    logger.info("Using SSL protocol for Manage app server: " + prop);
		    config.setSslProtocol(prop);
	    });

		propertyConsumerMap.put("mlManageSslHostnameVerifier", (config, prop) -> {
			logger.info("Manage SSL hostname verifier: " + prop);
			config.setSslHostnameVerifier(JavaClientUtil.toSSLHostnameVerifier(prop));
		});
		propertyConsumerMap.put("mlSslHostnameVerifier", (config, prop) -> {
			if (!propertyExists("mlManageSslHostnameVerifier")) {
				logger.info("Manage SSL hostname verifier: " + prop);
				config.setSslHostnameVerifier(JavaClientUtil.toSSLHostnameVerifier(prop));
			}
		});

	    propertyConsumerMap.put("mlManageUseDefaultKeystore", (config, prop) -> {
	    	logger.info("Using default JVM keystore for SSL for Manage app server: " + prop);
	    	config.setUseDefaultKeystore(Boolean.parseBoolean(prop));
	    });

	    propertyConsumerMap.put("mlManageTrustManagementAlgorithm", (config, prop) -> {
		    logger.info("Using trust management algorithm for SSL for Manage app server: " + prop);
		    config.setTrustManagementAlgorithm(prop);
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

		// Processed last so that it can override scheme/port
		propertyConsumerMap.put("mlCloudApiKey", (config, prop) -> {
			logger.info("Setting Manage cloud API key and forcing scheme to HTTPS and port to 443");
			config.setCloudApiKey(prop);
			config.setScheme("https");
			config.setPort(443);
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
