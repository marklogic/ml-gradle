package com.marklogic.appdeployer.mgmt.databases;

import org.junit.Before;
import org.junit.Test;

import com.marklogic.appdeployer.mgmt.AbstractMgmtTest;
import com.marklogic.appdeployer.mgmt.databases.DatabaseManager;
import com.marklogic.appdeployer.mgmt.forests.ForestManager;

public class CreateTriggersDatabaseForProjectTest extends AbstractMgmtTest {

    @Before
    public void setup() {
        initializeAppManager();
    }
    
    @Test
    public void createAndDelete() {
        DatabaseManager dbMgr = new DatabaseManager(manageClient);
        ForestManager forestMgr = new ForestManager(manageClient);

        String dbName = "sample-app-triggers";
        String forestName = dbName + "-1";

        appManager.createApp(appConfig, configDir);

        assertTrue("The triggers database should have been created", dbMgr.dbExists(dbName));
        assertTrue("A forest for the triggers database should have been created", forestMgr.forestExists(forestName));
        assertTrue("The forest should be attached", forestMgr.isForestAttached(forestName));

        deleteSampleApp();

        assertFalse("The triggers database should have been deleted", dbMgr.dbExists(dbName));
        assertFalse("The triggers forest should have been deleted", forestMgr.forestExists(forestName));
    }
}
