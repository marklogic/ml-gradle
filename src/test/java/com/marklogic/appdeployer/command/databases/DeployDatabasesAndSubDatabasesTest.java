package com.marklogic.appdeployer.command.databases;

import java.io.File;
import java.util.List;

import org.junit.Test;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.restapis.DeployRestApiServersCommand;
import com.marklogic.mgmt.resource.databases.DatabaseManager;

public class DeployDatabasesAndSubDatabasesTest extends AbstractAppDeployerTest{
    @Test
    public void test() {
        ConfigDir configDir = appConfig.getConfigDir();
        configDir.setBaseDir(new File("src/test/resources/sample-app/subdatabases"));

        initializeAppDeployer(new DeployRestApiServersCommand(), new DeployContentDatabasesCommand(2),
                new DeployTriggersDatabaseCommand(), new DeploySchemasDatabaseCommand(),
                new DeployOtherDatabasesCommand());

        DatabaseManager dbMgr = new DatabaseManager(manageClient);

        String[] dbNames = new String[] { "sample-app-content", "sample-app-triggers", "sample-app-schemas",
                "mysuperdb", "mysuperdb-subdb01", "mysuperdb-subdb02", "sample-app-content-subdb01", "sample-app-content-subdb01" };
        try {
            appDeployer.deploy(appConfig);

            for (String name : dbNames) {
                assertTrue("Expected to find database: " + name, dbMgr.exists(name));
            }
            
        	// check that subdatabases are associated 
            List<String> subDatabases = dbMgr.getSubDatabases("mysuperdb");
            assertTrue("Expected to find subdatabase of 'mysuperdb-subdb01'", subDatabases.contains("mysuperdb-subdb01"));
            assertTrue("Expected to find subdatabase of 'mysuperdb-subdb02'", subDatabases.contains("mysuperdb-subdb02"));

            subDatabases = dbMgr.getSubDatabases("sample-app-content");
            assertTrue("Expected to find subdatabase of 'sample-app-content-subdb01'", subDatabases.contains("sample-app-content-subdb01"));
            assertTrue("Expected to find subdatabase of 'sample-app-content-subdb02'", subDatabases.contains("sample-app-content-subdb02"));
            

        } finally {
            
            undeploySampleApp();

            for (String name : dbNames) {
                assertFalse("Expected to not find database: " + name, dbMgr.exists(name));
            }
            
            
        }
    }

}
