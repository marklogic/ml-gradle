package com.marklogic.appdeployer.command.databases;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.restapis.CreateRestApiServersCommand;
import com.marklogic.rest.mgmt.databases.DatabaseManager;
import com.marklogic.rest.util.Fragment;

public class UpdateContentDatabasesTest extends AbstractAppDeployerTest {

    private DatabaseManager dbMgr;
    private String idRangeIndexPath = "//m:range-element-index[m:scalar-type = 'string' and m:namespace-uri = 'urn:sampleapp' and m:localname='id' and m:collation='http://marklogic.com/collation/']";

    @Before
    public void setup() {
        dbMgr = new DatabaseManager(manageClient);
    }

    @After
    public void teardown() {
        undeploySampleApp();
    }

    @Test
    public void updateDatabase() {
        // We want both a main and a test app server in this test
        appConfig.setTestRestPort(SAMPLE_APP_TEST_REST_PORT);

        initializeAppDeployer(new CreateRestApiServersCommand(), new UpdateContentDatabasesCommand());

        appDeployer.deploy(appConfig);

        Fragment db = dbMgr.getDatabasePropertiesAsXml(appConfig.getContentDatabaseName());
        assertTrue(db.elementExists(idRangeIndexPath));

        db = dbMgr.getDatabasePropertiesAsXml(appConfig.getTestContentDatabaseName());
        assertTrue(db.elementExists(idRangeIndexPath));
    }

    @Test
    public void multipleDatabaseConfigFiles() throws Exception {
        ConfigDir dir = appConfig.getConfigDir();
        dir.getContentDatabaseFiles().add(new File(dir.getDatabasesDir(), "more-content-db-config.json"));

        initializeAppDeployer(new CreateRestApiServersCommand(), new UpdateContentDatabasesCommand());

        appDeployer.deploy(appConfig);

        String rangeIndexXpath = "//m:range-element-index[m:namespace-uri = 'urn:sampleapp' and m:localname='anotherElement']";

        Fragment db = dbMgr.getDatabasePropertiesAsXml(appConfig.getContentDatabaseName());
        assertTrue(db.elementExists("//m:maintain-last-modified[. = 'true']"));
        assertTrue(db.elementExists(idRangeIndexPath));
        assertTrue(db.elementExists(rangeIndexXpath));
    }
}
