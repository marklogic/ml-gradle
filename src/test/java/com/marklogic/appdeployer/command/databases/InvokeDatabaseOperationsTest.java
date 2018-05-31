package com.marklogic.appdeployer.command.databases;

import com.marklogic.mgmt.api.API;
import com.marklogic.mgmt.api.database.Database;
import com.marklogic.mgmt.mapper.DefaultResourceMapper;
import org.junit.After;
import org.junit.Test;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.mgmt.resource.databases.DatabaseManager;

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

        String json = mgr.getPropertiesAsJson(appConfig.getContentDatabaseName());
	    System.out.println(json);
        Database db = new DefaultResourceMapper(new API(manageClient)).readResource(json, Database.class);
	    assertEquals("ÉÉÉ", db.getRangeElementIndex().get(0).getLocalname());
    }
}
