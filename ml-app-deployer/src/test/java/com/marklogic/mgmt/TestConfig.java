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

import com.marklogic.mgmt.admin.DefaultAdminConfigFactory;
import com.marklogic.mgmt.util.SimplePropertySource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import com.marklogic.mgmt.admin.AdminConfig;

/**
 * Defines configuration for the JUnit tests. The non-version-controlled user.properties file is imported second so that
 * a developer can override what's in test.properties.
 */
@Configuration
@PropertySource(value = { "classpath:test.properties", "classpath:user.properties" }, ignoreResourceNotFound = true)
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
			"mlManageSimpleSsl", simpleSsl != null ? simpleSsl.toString() : null
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
			"mlAdminSimpleSsl", simpleSsl != null ? simpleSsl.toString() : null
		)).newAdminConfig();
    }
}
