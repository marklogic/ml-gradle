package com.marklogic.appdeployer.command.restapis;

import java.io.File;

import org.junit.Test;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.mgmt.appservers.ServerManager;
import com.marklogic.mgmt.databases.DatabaseManager;
import com.marklogic.mgmt.forests.ForestManager;

public class CreateRestApiWithCustomFilenameTest extends AbstractAppDeployerTest {

    @Test
    public void customFilename() {
        appConfig.setConfigDir(new ConfigDir(new File("src/test/resources/sample-app/rest-api-custom-filename")));

        initializeAppDeployer(new DeployRestApiServersCommand("my-custom-rest-api.json", true));

        ServerManager serverMgr = new ServerManager(manageClient);
        DatabaseManager dbMgr = new DatabaseManager(manageClient);
        ForestManager forestMgr = new ForestManager(manageClient);

        try {
            appDeployer.deploy(appConfig);

            assertTrue(serverMgr.exists("my-custom-rest-api"));
            assertTrue(dbMgr.exists("my-custom-content"));
            assertTrue(dbMgr.exists("my-custom-modules"));
            assertTrue(forestMgr.exists("my-custom-content-1"));
            assertFalse("The custom REST API file only asks for 1 forest", forestMgr.exists("my-custom-content-2"));
        } finally {
            undeploySampleApp();

            assertFalse(serverMgr.exists("my-custom-rest-api"));
            assertFalse(dbMgr.exists("my-custom-content"));
            assertFalse(dbMgr.exists("my-custom-modules"));
            assertFalse(forestMgr.exists("my-custom-content-1"));
        }
    }
}
