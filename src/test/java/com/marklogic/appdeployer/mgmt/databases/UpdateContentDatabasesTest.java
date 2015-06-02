package com.marklogic.appdeployer.mgmt.databases;

import org.junit.After;
import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.marklogic.appdeployer.app.RestApiConfiguration;
import com.marklogic.appdeployer.app.plugin.UpdateContentDatabasesPlugin;
import com.marklogic.appdeployer.mgmt.AbstractMgmtTest;
import com.marklogic.appdeployer.util.Fragment;

public class UpdateContentDatabasesTest extends AbstractMgmtTest {

    @Test
    public void updateDatabase() {
        appConfig.setTestRestPort(SAMPLE_APP_TEST_REST_PORT);
        initializeAppManager(UpdateContentDatabaseConfiguration.class);

        appManager.createApp(appConfig, configDir);

        String rangeIndexXpath = "/m:database-properties/m:range-element-indexes/m:range-element-index"
                + "[m:scalar-type = 'string' and m:namespace-uri = 'urn:sampleapp' and m:localname='id' and m:collation='http://marklogic.com/collation/']";
        DatabaseManager dbMgr = new DatabaseManager(manageClient);

        Fragment db = dbMgr.getDatabasePropertiesAsXml(appConfig.getContentDatabaseName());
        assertTrue(db.elementExists(rangeIndexXpath));

        db = dbMgr.getDatabasePropertiesAsXml(appConfig.getTestContentDatabaseName());
        assertTrue(db.elementExists(rangeIndexXpath));
    }

    @After
    public void teardown() {
        deleteSampleApp();
    }
}

@Configuration
class UpdateContentDatabaseConfiguration extends RestApiConfiguration {

    @Bean
    public UpdateContentDatabasesPlugin updateContentDatabasePlugin() {
        return new UpdateContentDatabasesPlugin();
    }
}