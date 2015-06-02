package com.marklogic.appdeployer.app.manager.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.marklogic.appdeployer.app.plugin.TriggersDatabasePlugin;

/**
 * Intended to be a useful configuration that will work for a wide variety of apps. Can of course be subclassed to add
 * additional plugins.
 */
@Configuration
public class DefaultConfiguration extends RestApiConfiguration {

    @Bean
    public TriggersDatabasePlugin triggersDatabasePlugin() {
        return new TriggersDatabasePlugin();
    }
}
