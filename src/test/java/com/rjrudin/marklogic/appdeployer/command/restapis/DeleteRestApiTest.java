package com.rjrudin.marklogic.appdeployer.command.restapis;

import java.io.File;

import org.junit.Test;

import com.rjrudin.marklogic.appdeployer.AbstractAppDeployerTest;
import com.rjrudin.marklogic.appdeployer.ConfigDir;
import com.rjrudin.marklogic.appdeployer.command.databases.CreateContentDatabasesCommand;
import com.rjrudin.marklogic.mgmt.appservers.ServerManager;
import com.rjrudin.marklogic.mgmt.databases.DatabaseManager;
import com.rjrudin.marklogic.mgmt.restapis.RestApiManager;

/**
 * This test ensures that the convenience methods for creating and deleting a sample application work properly, and thus
 * they can be used in other tests that depend on having an app in place.
 */
public class DeleteRestApiTest extends AbstractAppDeployerTest {

    @Test
    public void createAndDelete() {
        RestApiManager mgr = new RestApiManager(manageClient);
        ServerManager serverMgr = new ServerManager(manageClient, appConfig.getGroupName());

        initializeAppDeployer(new CreateRestApiServersCommand());
        appDeployer.deploy(appConfig);

        assertTrue("The REST API server should exist", mgr.restApiServerExists(SAMPLE_APP_NAME));
        assertTrue("The REST API app server should exist", serverMgr.exists(SAMPLE_APP_NAME));

        undeploySampleApp();
        assertFalse("The REST API server should have been deleted", mgr.restApiServerExists(SAMPLE_APP_NAME));
        assertFalse("The REST API app server have been deleted", serverMgr.exists(SAMPLE_APP_NAME));
    }

    @Test
    public void contentDatabaseCommandAndRestApiCommandConfiguredToDeleteContent() {
        DatabaseManager dbMgr = new DatabaseManager(manageClient);
        final String dbName = appConfig.getContentDatabaseName();

        CreateRestApiServersCommand command = new CreateRestApiServersCommand();
        command.setDeleteContentDatabase(true);
        initializeAppDeployer(new CreateRestApiServersCommand(), new CreateContentDatabasesCommand());

        appDeployer.deploy(appConfig);
        assertTrue("The content database should have been created by the REST API command", dbMgr.exists(dbName));

        undeploySampleApp();
        assertFalse(
                "The content database should have been deleted by the REST API command, and the database command shouldn't throw any error",
                dbMgr.exists(dbName));
    }

    @Test
    public void emptyConfigDirWithContentDatabaseCommand() {
        DatabaseManager dbMgr = new DatabaseManager(manageClient);
        final String dbName = appConfig.getContentDatabaseName();

        appConfig.setConfigDir(new ConfigDir(new File("src/test/resources/sample-app/empty-ml-config")));

        initializeAppDeployer(new CreateRestApiServersCommand(), new CreateContentDatabasesCommand());

        appDeployer.deploy(appConfig);
        assertTrue("The content database should have been created by the REST API command", dbMgr.exists(dbName));

        undeploySampleApp();
        assertFalse(
                "The content database should have been deleted by the content database command, even though a content-database.json file doesn't exist",
                dbMgr.exists(dbName));
    }

    @Test
    public void emptyConfigDirWithNoContentDatabaseCommand() {
        DatabaseManager dbMgr = new DatabaseManager(manageClient);
        final String dbName = appConfig.getContentDatabaseName();
        final String modulesDbName = appConfig.getModulesDatabaseName();

        appConfig.setConfigDir(new ConfigDir(new File("src/test/resources/sample-app/empty-ml-config")));

        CreateRestApiServersCommand command = new CreateRestApiServersCommand();
        assertFalse("By default, this command shouldn't delete the content database", command.isDeleteContentDatabase());
        assertTrue("By default, this command should delete the modules database", command.isDeleteModulesDatabase());

        command.setDeleteContentDatabase(true);
        initializeAppDeployer(command);

        appDeployer.deploy(appConfig);
        assertTrue("The content database should have been created by the REST API command", dbMgr.exists(dbName));
        assertTrue("The modules database should have been created by the REST API command", dbMgr.exists(modulesDbName));

        undeploySampleApp();
        assertFalse("The content database should have been deleted by REST API command", dbMgr.exists(dbName));
        assertFalse("The modules database should have been deleted by REST API command", dbMgr.exists(modulesDbName));
    }
}
