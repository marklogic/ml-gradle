package com.marklogic.appdeployer.command.databases;

import org.junit.Test;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.command.restapis.CreateRestApiServersCommand;
import com.marklogic.rest.mgmt.databases.DatabaseManager;
import com.marklogic.rest.mgmt.forests.ForestManager;

public class CreateTriggersDatabaseTest extends AbstractAppDeployerTest {

    @Test
    public void createAndDelete() {
        initializeAppDeployer(new CreateRestApiServersCommand(), new CreateTriggersDatabaseCommand());

        appDeployer.deploy(appConfig);

        DatabaseManager dbMgr = new DatabaseManager(manageClient);
        ForestManager forestMgr = new ForestManager(manageClient);

        String dbName = "sample-app-triggers";
        String forestName = dbName + "-1";

        assertTrue("The triggers database should have been created", dbMgr.dbExists(dbName));
        assertTrue("A forest for the triggers database should have been created", forestMgr.forestExists(forestName));
        assertTrue("The forest should be attached", forestMgr.isForestAttached(forestName));

        undeploySampleApp();

        assertFalse("The triggers database should have been deleted", dbMgr.dbExists(dbName));
        assertFalse("The triggers forest should have been deleted", forestMgr.forestExists(forestName));
    }
}
