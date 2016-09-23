package com.marklogic.client.spring.config;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.helper.DatabaseClientConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@ComponentScan({ "com.marklogic.client.helper" } )
@PropertySource("classpath:application.properties")
public class MarkLogicApplicationContext {
    
    /**
     * Ensures that placeholders are replaced with property values
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceHolderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
    
    @Bean
    public DatabaseClient databaseClient(DatabaseClientConfig databaseClientConfig) {
        return DatabaseClientFactory.newClient(
                databaseClientConfig.getHost(),
                databaseClientConfig.getPort(),
                databaseClientConfig.getUsername(),
                databaseClientConfig.getPassword(),
                databaseClientConfig.getAuthentication());
    }
    
}
