package com.rjrudin.marklogic.appdeployer.command.databases;

import org.junit.After;
import org.junit.Test;

import com.rjrudin.marklogic.appdeployer.AbstractAppDeployerTest;
import com.rjrudin.marklogic.appdeployer.command.modules.LoadModulesCommand;
import com.rjrudin.marklogic.appdeployer.command.restapis.DeployRestApiServersCommand;
import com.rjrudin.marklogic.mgmt.databases.DatabaseManager;

public class ClearDatabaseTest extends AbstractAppDeployerTest {

    @After
    public void teardown() {
        undeploySampleApp();
    }

    /**
     * Testing against the modules database, but the operation is the same regardless of database.
     * 
     * Also, for the modules database, ml-gradle 1.+ had the ability to exclude certain modules from being deleted in
     * case those modules were needed by the REST API rewriter. But in that case, it's usually preferable to just use
     * the XCC approach for loading asset modules.
     */
    @Test
    public void modulesDatabase() {
        initializeAppDeployer(new DeployRestApiServersCommand(), new LoadModulesCommand());

        appDeployer.deploy(appConfig);

        DatabaseManager mgr = new DatabaseManager(manageClient);
        mgr.clearDatabase(appConfig.getModulesDatabaseName());

        String uris = newModulesXccTemplate().executeAdhocQuery("cts:uris((), (), cts:and-query(()))");
        assertTrue("The modules database should have been cleared", uris.length() == 0);
    }
}
