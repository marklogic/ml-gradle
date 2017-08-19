package com.marklogic.client.ext.spring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import com.marklogic.client.ext.DatabaseClientConfig;
import com.marklogic.client.ext.helper.DatabaseClientProvider;
import com.marklogic.xcc.template.XccTemplate;

/**
 * Provides a basic configuration for Spring-based applications. Assumes that properties can be found in the
 * gradle.properties file, though that file does not need to exist - this can be subclassed and a different property
 * source can be used. And since this is using Spring's Value annotation, system properties can be used to set all of
 * the property values as well.
 *
 * For ML8, this now assumes that the XDBC server on port 8000 will be used, in which case in order to create an
 * XccTemplate, it needs to know the application name.
 */
@Configuration
@PropertySource(value = { "file:gradle.properties" }, ignoreResourceNotFound = true)
public class BasicConfig {

    @Value("${mlAppName}")
    private String mlAppName;

    @Value("${mlUsername:admin}")
    private String mlUsername;

    @Value("${mlPassword}")
    private String mlPassword;

    @Value("${mlHost:localhost}")
    private String mlHost;

    @Value("${mlRestPort:0}")
    private Integer mlRestPort;

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
    public DatabaseClientConfig databaseClientConfig() {
        return new DatabaseClientConfig(getMlHost(), getRestPort(), getMlUsername(), getMlPassword());
    }

    @Bean
    public XccTemplate xccTemplate() {
        return new XccTemplate(String.format("xcc://%s:%s@%s:8000/%s", getMlUsername(), getMlPassword(), getMlHost(),
                buildContentDatabaseName(mlAppName)));
    }

    /**
     * Used for building an XccTemplate; can be overridden when this method's assumption about the name isn't true.
     */
    protected String buildContentDatabaseName(String mlAppName) {
        return mlAppName + "-content";
    }

    @Bean
    public DatabaseClientProvider databaseClientProvider() {
        return new SimpleDatabaseClientProvider(databaseClientConfig());
    }

    protected Integer getRestPort() {
        return mlRestPort;
    }

    public String getMlUsername() {
        return mlUsername;
    }

    public String getMlPassword() {
        return mlPassword;
    }

    public String getMlHost() {
        return mlHost;
    }

    public Integer getMlRestPort() {
        return mlRestPort;
    }

    public String getMlAppName() {
        return mlAppName;
    }

    public void setMlAppName(String mlAppName) {
        this.mlAppName = mlAppName;
    }

}
