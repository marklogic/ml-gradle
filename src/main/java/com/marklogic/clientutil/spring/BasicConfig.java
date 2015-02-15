package com.marklogic.clientutil.spring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import com.marklogic.clientutil.DatabaseClientConfig;
import com.marklogic.clientutil.DatabaseClientProvider;
import com.marklogic.xccutil.template.XccTemplate;

/**
 * Provides a basic configuration for Spring-based applications. Assumes that properties can be found in the
 * gradle.properties file, though that file does not need to exist - this can be subclassed and a different property
 * source can be used. And since this is using Spring's Value annotation, system properties can be used to set all of
 * the property values as well.
 */
@Configuration
@PropertySource({ "file:gradle.properties" })
public class BasicConfig {

    @Value("${mlUsername:admin}")
    private String mlUsername;

    @Value("${mlPassword}")
    private String mlPassword;

    @Value("${mlHost:localhost}")
    private String mlHost;

    @Value("${mlRestPort:0}")
    private Integer mlRestPort;

    @Value("${mlXdbcPort:0}")
    private Integer mlXdbcPort;

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
        return new XccTemplate(String.format("xcc://%s:%s@%s:%d", getMlUsername(), getMlPassword(), getMlHost(),
                getXdbcPort()));
    }

    @Bean
    public DatabaseClientProvider databaseClientProvider() {
        return new SimpleDatabaseClientProvider();
    }

    protected Integer getRestPort() {
        return mlRestPort;
    }

    protected Integer getXdbcPort() {
        return mlXdbcPort;
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

    public Integer getMlXdbcPort() {
        return mlXdbcPort;
    }

}
