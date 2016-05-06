package com.marklogic.appdeployer.command.databases;

import org.junit.After;
import org.junit.Test;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.mgmt.databases.DatabaseManager;

public class InvokeDatabaseOperationsTest extends AbstractAppDeployerTest {

    @After
    public void teardown() {
        undeploySampleApp();
    }

    @Test
    public void test() {
        setConfigBaseDir("sample-app/db-only-config");

        initializeAppDeployer(new DeployContentDatabasesCommand(1));
        deploySampleApp();

        DatabaseManager mgr = new DatabaseManager(manageClient);

        // Just smoke-testing these to make sure they don't blow up
        mgr.mergeDatabase(appConfig.getContentDatabaseName());
        mgr.reindexDatabase(appConfig.getContentDatabaseName());
    }
}
