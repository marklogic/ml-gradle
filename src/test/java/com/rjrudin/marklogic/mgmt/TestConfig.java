package com.rjrudin.marklogic.mgmt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import com.rjrudin.marklogic.mgmt.admin.AdminConfig;

/**
 * Defines configuration for the JUnit tests. The non-version-controlled user.properties file is imported second so that
 * a developer can override what's in test.properties.
 */
@Configuration
@PropertySource(value = { "classpath:test.properties", "classpath:user.properties" }, ignoreResourceNotFound = true)
public class TestConfig {

    @Value("${mlManageHost:localhost}")
    private String mlManageHost;

    @Value("${mlManageUsername:admin}")
    private String mlManageUsername;

    @Value("${mlManagePassword}")
    private String mlManagePassword;

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
        return new ManageConfig(getMlManageHost(), 8002, getMlManageUsername(), getMlManagePassword());
    }

    /**
     * For now, assume the username/password works for 8001 too. Easy to make this configurable later if needed.
     */
    @Bean
    public AdminConfig adminConfig() {
        return new AdminConfig(getMlManageHost(), 8001, getMlManageUsername(), getMlManagePassword());
    }

    
    public String getMlManageHost() {
        return mlManageHost;
    }

    public String getMlManageUsername() {
        return mlManageUsername;
    }

    public String getMlManagePassword() {
        return mlManagePassword;
    }
}
