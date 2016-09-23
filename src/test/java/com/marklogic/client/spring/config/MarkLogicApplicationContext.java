package com.marklogic.client.spring.config;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.helper.DatabaseClientConfig;
import com.marklogic.client.helper.DatabaseClientProvider;
import com.marklogic.client.spring.SimpleDatabaseClientProvider;
import com.marklogic.xcc.template.XccTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@ComponentScan({ "com.marklogic.client.helper" } )
@PropertySource("classpath:application.properties")
public class MarkLogicApplicationContext {
    
    @Value("${mlAppName}")
    private String mlAppName;
    
    /**
     * Ensures that placeholders are replaced with property values
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceHolderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
    
    @Bean
    public DatabaseClientProvider databaseClientProvider(DatabaseClientConfig databaseClientConfig) {
        return new SimpleDatabaseClientProvider(databaseClientConfig);
    }
    
    @Bean
    public XccTemplate xccTemplate(DatabaseClientConfig databaseClientConfig) {
        return new XccTemplate(
                String.format("xcc://%s:%s@%s:8000/%s",
                        databaseClientConfig.getUsername(),
                        databaseClientConfig.getPassword(),
                        databaseClientConfig.getHost(),
                        mlAppName + "-content"));
    }
    
}
