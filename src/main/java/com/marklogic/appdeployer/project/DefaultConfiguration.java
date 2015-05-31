package com.marklogic.appdeployer.project;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DefaultConfiguration {

    @Bean
    public RestApiPlugin restApiPlugin() {
        return new RestApiPlugin();
    }

    @Bean
    public TriggersDatabasePlugin triggersDatabasePlugin() {
        return new TriggersDatabasePlugin();
    }
}
