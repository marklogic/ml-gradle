package com.marklogic.appdeployer.mgmt.databases;

import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.marklogic.appdeployer.mgmt.AbstractMgmtTest;
import com.marklogic.appdeployer.project.plugin.RestApiPlugin;
import com.marklogic.appdeployer.project.plugin.UpdateContentDatabasePlugin;

public class UpdateDatabaseTest extends AbstractMgmtTest {

    /**
     * Submit a configuration to ProjectManager that creates a REST API and then updates the database because it finds a
     * "content-database.json" file.
     */
    @Test
    public void updateDatabase() {
        initializeProjectManager(TestConfiguration.class);
    }
}

@Configuration
class TestConfiguration {

    @Bean
    public RestApiPlugin restApiPlugin() {
        return new RestApiPlugin();
    }

    @Bean
    public UpdateContentDatabasePlugin updateContentDatabasePlugin() {
        return new UpdateContentDatabasePlugin();
    }
}