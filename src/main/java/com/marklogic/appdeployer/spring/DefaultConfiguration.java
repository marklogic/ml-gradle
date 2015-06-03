package com.marklogic.appdeployer.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.marklogic.appdeployer.plugin.databases.CreateTriggersDatabasePlugin;

/**
 * Intended to be a useful configuration that will work for a wide variety of apps. Can of course be subclassed to add
 * additional plugins.
 */
@Configuration
public class DefaultConfiguration extends RestApiConfiguration {

    @Bean
    public CreateTriggersDatabasePlugin triggersDatabasePlugin() {
        return new CreateTriggersDatabasePlugin();
    }
}
