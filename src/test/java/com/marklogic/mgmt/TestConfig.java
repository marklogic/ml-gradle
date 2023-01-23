package com.marklogic.mgmt;

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

    @Value("${mlManageHost:localhost}")
    private String host;

	@Value("${mlManagePort:8002}")
	private Integer managePort;

	@Value("${mlAdminPort:8001}")
	private Integer adminPort;

    @Value("${mlManageUsername:admin}")
    private String username;

    @Value("${mlManagePassword:}")
    private String password;

	@Value("${mlBasePath:}")
	private String basePath;

	@Value("${mlCloudApiKey:}")
	private String cloudApiKey;

	@Value("${mlScheme:http}")
	private String scheme;

	@Value("${mlSimpleSsl:false}")
	private boolean simpleSsl;

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
        ManageConfig config = new ManageConfig(host, managePort, username, password);
		config.setBasePath(basePath);
		config.setCloudApiKey(cloudApiKey);
		config.setScheme(scheme);
		if (simpleSsl) {
			config.setConfigureSimpleSsl(true);
		}
        // Clean the JSON by default
	    config.setCleanJsonPayloads(true);
	    return config;
    }

    /**
     * For now, assume the username/password works for 8001 too. Easy to make this configurable later if needed.
     */
    @Bean
    public AdminConfig adminConfig() {
        AdminConfig config = new AdminConfig(host, adminPort, username, password);
		config.setBasePath(basePath);
		config.setCloudApiKey(cloudApiKey);
		config.setScheme(scheme);
		if (simpleSsl) {
			config.setConfigureSimpleSsl(true);
		}
		return config;
    }
}
