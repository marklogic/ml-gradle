package com.rjrudin.marklogic.appdeployer.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.rjrudin.marklogic.appdeployer.command.databases.CreateTriggersDatabaseCommand;

/**
 * Intended to be a useful configuration that will work for a wide variety of apps. Can of course be subclassed to add
 * additional commands.
 */
@Configuration
public class DefaultConfiguration extends RestApiConfiguration {

    @Bean
    public CreateTriggersDatabaseCommand createTriggersDatabaseCommand() {
        return new CreateTriggersDatabaseCommand();
    }
}
