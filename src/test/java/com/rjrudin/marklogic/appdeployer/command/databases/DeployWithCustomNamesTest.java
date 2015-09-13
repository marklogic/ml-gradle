package com.rjrudin.marklogic.appdeployer.command.databases;

import org.junit.Test;

import com.rjrudin.marklogic.appdeployer.AbstractAppDeployerTest;
import com.rjrudin.marklogic.appdeployer.command.restapis.DeployRestApiServersCommand;
import com.rjrudin.marklogic.mgmt.appservers.ServerManager;
import com.rjrudin.marklogic.mgmt.databases.DatabaseManager;
import com.rjrudin.marklogic.mgmt.forests.ForestManager;

/**
 * Verifies that all the default names in AppConfig can be overridden successfully.
 */
public class DeployWithCustomNamesTest extends AbstractAppDeployerTest {

    private DatabaseManager dbMgr;
    private ForestManager forestMgr;
    private ServerManager serverMgr;

    private String prefix = "custom-sample-app";

    @Test
    public void test() {
        appConfig.setTestRestPort(SAMPLE_APP_TEST_REST_PORT);

        appConfig.setRestServerName(prefix);
        appConfig.setTestRestServerName(prefix + "-test");
        appConfig.setContentDatabaseName(prefix + "-content");
        appConfig.setTestContentDatabaseName(prefix + "-test-content");
        appConfig.setModulesDatabaseName(prefix + "-modules");
        appConfig.setTriggersDatabaseName(prefix + "-triggers");
        appConfig.setSchemasDatabaseName(prefix + "-schemas");

        DeployContentDatabasesCommand dcdc = new DeployContentDatabasesCommand();
        dcdc.setForestsPerHost(2);
        DeployTriggersDatabaseCommand dtdc = new DeployTriggersDatabaseCommand();
        dtdc.setForestsPerHost(1);
        DeploySchemasDatabaseCommand dsdc = new DeploySchemasDatabaseCommand();
        dsdc.setForestsPerHost(1);

        initializeAppDeployer(new DeployRestApiServersCommand(), dcdc, dtdc, dsdc);

        dbMgr = new DatabaseManager(manageClient);
        forestMgr = new ForestManager(manageClient);
        serverMgr = new ServerManager(manageClient, "Default");

        try {
            appDeployer.deploy(appConfig);
            assertResourcesExist(true);
        } finally {
            undeploySampleApp();
            assertResourcesExist(false);
        }
    }

    private void assertResourcesExist(boolean exists) {
        assertEquals(exists, dbMgr.exists(prefix + "-content"));
        assertEquals(exists, dbMgr.exists(prefix + "-test-content"));
        assertEquals(exists, dbMgr.exists(prefix + "-triggers"));
        assertEquals(exists, dbMgr.exists(prefix + "-schemas"));
        assertEquals(exists, dbMgr.exists(prefix + "-modules"));

        // Assumes two content forests
        assertEquals(exists, forestMgr.exists(prefix + "-content-1"));
        assertEquals(exists, forestMgr.exists(prefix + "-content-2"));
        assertFalse(forestMgr.exists(prefix + "-content-3"));

        // Assumes two test-content forests
        assertEquals(exists, forestMgr.exists(prefix + "-test-content-1"));
        assertEquals(exists, forestMgr.exists(prefix + "-test-content-2"));
        assertFalse(forestMgr.exists(prefix + "-test-content-3"));

        // Assumes one forest
        assertEquals(exists, forestMgr.exists(prefix + "-triggers-1"));
        assertFalse(forestMgr.exists(prefix + "-triggers-2"));

        // Assumes one forest
        assertEquals(exists, forestMgr.exists(prefix + "-schemas-1"));
        assertFalse(forestMgr.exists(prefix + "-schemas-2"));

        // Assumes one forest
        assertEquals(exists, forestMgr.exists(prefix + "-modules-1"));
        assertFalse(forestMgr.exists(prefix + "-modules-2"));

        assertEquals(exists, serverMgr.exists(prefix));
        assertEquals(exists, serverMgr.exists(prefix + "-test"));
    }
}
