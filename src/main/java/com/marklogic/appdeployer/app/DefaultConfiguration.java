package com.marklogic.appdeployer.app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.marklogic.appdeployer.app.plugin.RestApiPlugin;
import com.marklogic.appdeployer.app.plugin.TriggersDatabasePlugin;

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
