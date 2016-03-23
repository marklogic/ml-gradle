package com.marklogic.appdeployer.command.databases;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.mgmt.databases.DatabaseManager;
import com.marklogic.rest.util.Fragment;
import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.util.List;

/**
 * Verifies support for bumping up the number of content forests and then re-deploying. Does not yet support lowering
 * the number of content forests and expecting the existing ones to be detached/deleted.
 */
public class UpdateForestCountTest extends AbstractAppDeployerTest {

    @Test
    public void test() {
        appConfig.getConfigDir().setBaseDir(new File("src/test/resources/sample-app/db-only-config"));
        DatabaseManager mgr = new DatabaseManager(manageClient);

        initializeAppDeployer(new DeployContentDatabasesCommand());

        appConfig.setContentForestsPerHost(1);
        appDeployer.deploy(appConfig);
        assertEquals("Should only have 1 forest", 1, mgr.getForestIds(appConfig.getContentDatabaseName()).size());

        appConfig.setContentForestsPerHost(2);
        appDeployer.deploy(appConfig);
        assertEquals("Should now have 2 forests", 2, mgr.getForestIds(appConfig.getContentDatabaseName()).size());

        appDeployer.deploy(appConfig);
        assertEquals("Should still have 2 forests", 2, mgr.getForestIds(appConfig.getContentDatabaseName()).size());

        appConfig.setContentForestsPerHost(1);
        appDeployer.deploy(appConfig);
        assertEquals("Should still have 2 forests, we don't yet support deleting forests when the number drops", 2, mgr.getForestIds(appConfig.getContentDatabaseName()).size());
    }

    @After
    public void teardown() {
        undeploySampleApp();
    }
}
