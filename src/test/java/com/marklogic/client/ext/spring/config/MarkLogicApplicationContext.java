package com.marklogic.client.ext.spring.config;

import com.marklogic.client.ext.helper.DatabaseClientConfig;
import com.marklogic.client.ext.helper.DatabaseClientProvider;
import com.marklogic.client.ext.spring.SimpleDatabaseClientProvider;
import com.marklogic.client.ext.spring.SpringDatabaseClientConfig;
import com.marklogic.xcc.template.XccTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@Import(value = { SpringDatabaseClientConfig.class } )
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
