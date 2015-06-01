package com.marklogic.appdeployer.mgmt.databases;

import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.marklogic.appdeployer.mgmt.AbstractMgmtTest;
import com.marklogic.appdeployer.project.plugin.RestApiPlugin;
import com.marklogic.appdeployer.project.plugin.UpdateContentDatabasesPlugin;

public class UpdateContentDatabasesTest extends AbstractMgmtTest {

    @Test
    public void updateDatabase() {
        initializeAppManager(TestConfiguration.class);

        appManager.createApp(appConfig, configDir);

        // TODO Verify the range index in the content database file was added
    }
}

@Configuration
class TestConfiguration {

    @Bean
    public RestApiPlugin restApiPlugin() {
        return new RestApiPlugin();
    }

    @Bean
    public UpdateContentDatabasesPlugin updateContentDatabasePlugin() {
        return new UpdateContentDatabasesPlugin();
    }
}