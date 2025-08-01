/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.command.databases;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.mgmt.api.API;
import com.marklogic.mgmt.api.database.Database;
import com.marklogic.mgmt.mapper.DefaultResourceMapper;
import com.marklogic.mgmt.resource.databases.DatabaseManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InvokeDatabaseOperationsTest extends AbstractAppDeployerTest {

    @AfterEach
    public void teardown() {
        undeploySampleApp();
    }

    @Test
    public void test() {
        setConfigBaseDir("sample-app/db-only-config");

        initializeAppDeployer(new DeployOtherDatabasesCommand(1));
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
