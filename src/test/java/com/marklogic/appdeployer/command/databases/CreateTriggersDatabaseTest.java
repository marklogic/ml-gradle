package com.marklogic.appdeployer.command.databases;

import java.io.File;

import org.junit.Test;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.mgmt.resource.databases.DatabaseManager;
import com.marklogic.mgmt.resource.forests.ForestManager;

public class CreateTriggersDatabaseTest extends AbstractAppDeployerTest {

    @Test
    public void createAndDelete() {
        initializeAppDeployer(new DeployTriggersDatabaseCommand());

        appDeployer.deploy(appConfig);

        DatabaseManager dbMgr = new DatabaseManager(manageClient);
        ForestManager forestMgr = new ForestManager(manageClient);

        String dbName = appConfig.getTriggersDatabaseName();
        String forestName = dbName + "-1";

        try {
            assertTrue("The triggers database should have been created", dbMgr.exists(dbName));
            assertTrue("A forest for the triggers database should have been created",
                    forestMgr.forestExists(forestName));
            assertTrue("The forest should be attached", forestMgr.isForestAttached(forestName));
        } finally {
            undeploySampleApp();
            assertFalse("The triggers database should have been deleted", dbMgr.exists(dbName));
            assertFalse("The triggers forest should have been deleted", forestMgr.forestExists(forestName));
        }
    }

    @Test
    public void createViaAppConfigProperty() {
        appConfig.setConfigDir(new ConfigDir(new File("src/test/resources/sample-app/empty-ml-config")));

        initializeAppDeployer(new DeployTriggersDatabaseCommand());
        appDeployer.deploy(appConfig);

        DatabaseManager dbMgr = new DatabaseManager(manageClient);
        String dbName = appConfig.getTriggersDatabaseName();

        try {
            assertTrue("The triggers database should have been created", dbMgr.exists(dbName));
        } finally {
            undeploySampleApp();
            assertFalse("The triggers database should have been deleted", dbMgr.exists(dbName));
        }
    }

    @Test
    public void configPropertyIsSetToFalse() {
        appConfig.setConfigDir(new ConfigDir(new File("src/test/resources/sample-app/empty-ml-config")));
        appConfig.setCreateTriggersDatabase(false);

        initializeAppDeployer(new DeployTriggersDatabaseCommand());
        appDeployer.deploy(appConfig);

        DatabaseManager dbMgr = new DatabaseManager(manageClient);
        String dbName = appConfig.getTriggersDatabaseName();

        try {
            assertFalse(
                    "No triggers database should have been created since the config directory doesn't have a triggers file and the property is set to false",
                    dbMgr.exists(dbName));
        } finally {
            undeploySampleApp();
        }

    }
}
