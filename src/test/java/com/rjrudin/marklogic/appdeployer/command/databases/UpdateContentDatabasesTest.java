package com.rjrudin.marklogic.appdeployer.command.databases;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.rjrudin.marklogic.appdeployer.AbstractAppDeployerTest;
import com.rjrudin.marklogic.appdeployer.ConfigDir;
import com.rjrudin.marklogic.appdeployer.command.restapis.DeployRestApiServersCommand;
import com.rjrudin.marklogic.mgmt.databases.DatabaseManager;
import com.rjrudin.marklogic.rest.util.Fragment;

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

        initializeAppDeployer(new DeployRestApiServersCommand(), new DeployTriggersDatabaseCommand(),
                new DeployContentDatabasesCommand(), new DeploySchemasDatabaseCommand());

        appDeployer.deploy(appConfig);

        Fragment db = dbMgr.getPropertiesAsXml(appConfig.getContentDatabaseName());
        assertTrue(db.elementExists(idRangeIndexPath));

        db = dbMgr.getPropertiesAsXml(appConfig.getTestContentDatabaseName());
        assertTrue(db.elementExists(idRangeIndexPath));
    }

    @Test
    public void multipleDatabaseConfigFiles() throws Exception {
        ConfigDir dir = appConfig.getConfigDir();
        dir.getContentDatabaseFiles().add(new File(dir.getDatabasesDir(), "more-content-db-config.json"));

        initializeAppDeployer(new DeployRestApiServersCommand(), new DeployTriggersDatabaseCommand(),
                new DeployContentDatabasesCommand(), new DeploySchemasDatabaseCommand());

        appDeployer.deploy(appConfig);

        String rangeIndexXpath = "//m:range-element-index[m:namespace-uri = 'urn:sampleapp' and m:localname='anotherElement']";

        Fragment db = dbMgr.getPropertiesAsXml(appConfig.getContentDatabaseName());
        assertTrue(db.elementExists("//m:maintain-last-modified[. = 'true']"));
        assertTrue(db.elementExists(idRangeIndexPath));
        assertTrue(db.elementExists(rangeIndexXpath));
    }
}
