package com.marklogic.appdeployer.manager.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.marklogic.appdeployer.plugin.RestApiPlugin;

/**
 * Very simple configuration for when you only want a REST API server created.
 */
@Configuration
public class RestApiConfiguration {

    @Bean
    public RestApiPlugin restApiPlugin() {
        return new RestApiPlugin();
    }

}
