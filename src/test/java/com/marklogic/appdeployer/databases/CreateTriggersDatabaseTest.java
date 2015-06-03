package com.marklogic.appdeployer.databases;

import org.junit.Before;
import org.junit.Test;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.plugin.databases.CreateTriggersDatabasePlugin;
import com.marklogic.appdeployer.plugin.restapis.CreateRestApiServersPlugin;
import com.marklogic.rest.mgmt.databases.DatabaseManager;
import com.marklogic.rest.mgmt.forests.ForestManager;

public class CreateTriggersDatabaseTest extends AbstractAppDeployerTest {

    @Before
    public void setup() {
        initializeAppDeployer(new CreateRestApiServersPlugin(), new CreateTriggersDatabasePlugin());
    }

    @Test
    public void createAndDelete() {
        DatabaseManager dbMgr = new DatabaseManager(manageClient);
        ForestManager forestMgr = new ForestManager(manageClient);

        String dbName = "sample-app-triggers";
        String forestName = dbName + "-1";

        appDeployer.deploy(appConfig, configDir);

        assertTrue("The triggers database should have been created", dbMgr.dbExists(dbName));
        assertTrue("A forest for the triggers database should have been created", forestMgr.forestExists(forestName));
        assertTrue("The forest should be attached", forestMgr.isForestAttached(forestName));

        undeploySampleApp();

        assertFalse("The triggers database should have been deleted", dbMgr.dbExists(dbName));
        assertFalse("The triggers forest should have been deleted", forestMgr.forestExists(forestName));
    }
}
