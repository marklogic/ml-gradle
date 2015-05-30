package com.marklogic.appdeployer.mgmt;

import org.junit.Test;

import com.marklogic.appdeployer.mgmt.databases.DatabaseManager;

public class CreateTriggersDatabaseForProjectTest extends AbstractMgmtTest {

    @Test
    public void test() {
        createSampleApp();

        configMgr.createTriggersDatabase(configDir, appConfig);

        DatabaseManager mgr = new DatabaseManager(manageClient);
        assertTrue("The triggers database should have been created", mgr.dbExists("sample-app-triggers"));

        configMgr.setAdminConfig(new AdminConfig());
        // deleteSampleApp();
    }
}
