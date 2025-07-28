/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt;

import com.marklogic.mgmt.admin.AdminConfig;
import com.marklogic.mgmt.admin.DefaultAdminConfigFactory;
import com.marklogic.mgmt.util.SimplePropertySource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * Defines configuration for the JUnit tests. The non-version-controlled user.properties file is imported second so that
 * a developer can override what's in test.properties.
 */
@Configuration
@PropertySource(value = {"classpath:test.properties", "classpath:user.properties"}, ignoreResourceNotFound = true)
public class TestConfig {

	@Value("${mlHost:localhost}")
	private String host;

	@Value("${mlManagePort:#{NULL}}")
	private Integer managePort;

	@Value("${mlAdminPort:#{NULL}}")
	private Integer adminPort;

	@Value("${mlUsername:#{NULL}}")
	private String username;

	@Value("${mlPassword:#{NULL}}")
	private String password;

	@Value("${mlBasePath:#{NULL}}")
	private String basePath;

	@Value("${mlCloudApiKey:#{NULL}}")
	private String cloudApiKey;

	@Value("${mlScheme:#{NULL}}")
	private String scheme;

	@Value("${mlSimpleSsl:#{NULL}}")
	private Boolean simpleSsl;

	/**
	 * Has to be static so that Spring instantiates it first.
	 */
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyConfigurer() {
		PropertySourcesPlaceholderConfigurer c = new PropertySourcesPlaceholderConfigurer();
		c.setIgnoreResourceNotFound(true);
		return c;
	}

	@Bean
	public ManageConfig manageConfig() {
		ManageConfig config = new DefaultManageConfigFactory(new SimplePropertySource(
			"mlHost", host,
			"mlManagePort", managePort != null ? managePort.toString() : null,
			"mlUsername", username,
			"mlPassword", password,
			"mlManageBasePath", basePath,
			"mlCloudApiKey", cloudApiKey,
			"mlManageScheme", scheme,
			"mlManageSimpleSsl", simpleSsl != null ? simpleSsl.toString() : null,
			"mlRetryConnectionFailure", "true",
			"mlRetryDelayMultiplier", "2",
			"mlRetryMaxAttempts", "3",
			"mlRetryMaxDelay", "5000"
		)).newManageConfig();

		// Clean the JSON by default
		config.setCleanJsonPayloads(true);

		return config;
	}

	/**
	 * For now, assume the username/password works for 8001 too. Easy to make this configurable later if needed.
	 */
	@Bean
	public AdminConfig adminConfig() {
		return new DefaultAdminConfigFactory(new SimplePropertySource(
			"mlHost", host,
			"mlAdminPort", adminPort != null ? adminPort.toString() : null,
			"mlUsername", username,
			"mlPassword", password,
			"mlAdminBasePath", basePath,
			"mlCloudApiKey", cloudApiKey,
			"mlAdminScheme", scheme,
			"mlAdminSimpleSsl", simpleSsl != null ? simpleSsl.toString() : null,
			"mlRetryConnectionFailure", "true",
			"mlRetryDelayMultiplier", "2",
			"mlRetryMaxAttempts", "3",
			"mlRetryMaxDelay", "5000"
		)).newAdminConfig();
	}
}
