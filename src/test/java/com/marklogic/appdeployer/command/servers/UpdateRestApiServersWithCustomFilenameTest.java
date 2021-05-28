package com.marklogic.appdeployer.command.servers;

import java.io.File;

import org.junit.jupiter.api.Test;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.appservers.UpdateRestApiServersCommand;
import com.marklogic.appdeployer.command.restapis.DeployRestApiServersCommand;
import com.marklogic.mgmt.resource.appservers.ServerManager;
import com.marklogic.mgmt.resource.databases.DatabaseManager;
import com.marklogic.mgmt.resource.forests.ForestManager;
import com.marklogic.rest.util.Fragment;

import static org.junit.jupiter.api.Assertions.*;

public class UpdateRestApiServersWithCustomFilenameTest extends AbstractAppDeployerTest {
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
            assertFalse(forestMgr.exists("my-custom-content-2"), "The custom REST API file only asks for 1 forest");

            Fragment appServer = serverMgr.getPropertiesAsXml("my-custom-rest-api");
            assertEquals(appServer.getElementValue("//m:default-error-format"), "xml");

            initializeAppDeployer(new DeployRestApiServersCommand("my-custom-rest-api.json", true), new UpdateRestApiServersCommand("my-custom-rest-api-update.json"));
            appDeployer.deploy(appConfig);

            assertTrue(serverMgr.exists("my-custom-rest-api"));
            assertTrue(dbMgr.exists("my-custom-content"));
            assertTrue(dbMgr.exists("my-custom-modules"));
            assertTrue(forestMgr.exists("my-custom-content-1"));
            assertFalse(forestMgr.exists("my-custom-content-2"), "The custom REST API file only asks for 1 forest");

            appServer = serverMgr.getPropertiesAsXml("my-custom-rest-api");
            assertEquals(appServer.getElementValue("//m:default-error-format"), "json");
        } finally {
            undeploySampleApp();

            assertFalse(serverMgr.exists("my-custom-rest-api"));
            assertFalse(dbMgr.exists("my-custom-content"));
            assertFalse(dbMgr.exists("my-custom-modules"));
            assertFalse(forestMgr.exists("my-custom-content-1"));
        }
    }
}
