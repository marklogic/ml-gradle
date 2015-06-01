package com.marklogic.appdeployer.app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.marklogic.appdeployer.project.plugin.RestApiPlugin;
import com.marklogic.appdeployer.project.plugin.TriggersDatabasePlugin;

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
