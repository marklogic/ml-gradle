package com.marklogic.appdeployer.command.databases;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.mgmt.resource.databases.DatabaseManager;
import com.marklogic.mgmt.resource.forests.ForestManager;
import org.junit.After;
import org.junit.Test;

import java.io.File;

/**
 * Doesn't actually use a command, but it's nice to extend the parent test class.
 */
public class SetUpdatesAllowedOnDatabaseForestsTest extends AbstractAppDeployerTest {

    @After
    public void teardown() {
        undeploySampleApp();
    }

    @Test
    public void test() {
        appConfig.getFirstConfigDir().setBaseDir(new File("src/test/resources/sample-app/db-only-config"));
        initializeAppDeployer(new DeployOtherDatabasesCommand(2));
        appDeployer.deploy(appConfig);

        DatabaseManager dbMgr = new DatabaseManager(this.manageClient);
        dbMgr.setUpdatesAllowedOnPrimaryForests(appConfig.getContentDatabaseName(), "flash-backup");

        try {
	        ForestManager forestMgr = new ForestManager(this.manageClient);
	        assertEquals("flash-backup", forestMgr.getPropertiesAsXml("sample-app-content-1").getElementValue("//m:updates-allowed"));
	        assertEquals("flash-backup", forestMgr.getPropertiesAsXml("sample-app-content-2").getElementValue("//m:updates-allowed"));
        } finally {
	        // Gotta set it back to all so the database/forests can be deleted
	        dbMgr.setUpdatesAllowedOnPrimaryForests(appConfig.getContentDatabaseName(), "all");
        }
    }
}
