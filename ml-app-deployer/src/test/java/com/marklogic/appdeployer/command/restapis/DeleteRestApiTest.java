/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.appdeployer.command.restapis;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.databases.DeployOtherDatabasesCommand;
import com.marklogic.mgmt.api.API;
import com.marklogic.mgmt.api.database.Database;
import com.marklogic.mgmt.api.server.Server;
import com.marklogic.mgmt.resource.appservers.ServerManager;
import com.marklogic.mgmt.resource.databases.DatabaseManager;
import com.marklogic.mgmt.resource.restapis.RestApiManager;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * This test ensures that the convenience methods for creating and deleting a sample application work properly, and thus
 * they can be used in other tests that depend on having an app in place.
 */
public class DeleteRestApiTest extends AbstractAppDeployerTest {

    @Test
    public void createAndDelete() {
        RestApiManager mgr = new RestApiManager(manageClient);
        ServerManager serverMgr = new ServerManager(manageClient, appConfig.getGroupName());

        initializeAppDeployer(new DeployRestApiServersCommand(true));
        appDeployer.deploy(appConfig);

        assertTrue(mgr.restApiServerExists(SAMPLE_APP_NAME), "The REST API server should exist");
        assertTrue(serverMgr.exists(SAMPLE_APP_NAME), "The REST API app server should exist");

        undeploySampleApp();
        assertFalse(mgr.restApiServerExists(SAMPLE_APP_NAME), "The REST API server should have been deleted");
        assertFalse(serverMgr.exists(SAMPLE_APP_NAME), "The REST API app server have been deleted");
    }

	@Test
	@Disabled("This fails due to a server bug - BUG-60358")
	void deleteWithFilesystemAsModulesDatabase() {
		initializeAppDeployer(new DeployRestApiServersCommand(true));
		appDeployer.deploy(appConfig);

		API api = new API(manageClient);

		// Set the modules-database to the "filesystem", which will cause the DELETE to /v1/rest-apis/to fail due to
		// the server bug.
		Server server = new Server(api, appConfig.getRestServerName());
		server.setModulesDatabase("0");
		server.save();

		try {
			appDeployer.undeploy(appConfig);
		} finally {
			// Delete the modules-database to ensure it's not left around
			new Database(api, appConfig.getModulesDatabaseName()).delete();
		}
	}

    @Test
    public void contentDatabaseCommandAndRestApiCommandConfiguredToDeleteContent() {
        DatabaseManager dbMgr = new DatabaseManager(manageClient);
        final String dbName = appConfig.getContentDatabaseName();

        DeployRestApiServersCommand command = new DeployRestApiServersCommand();
        command.setDeleteContentDatabase(true);
        initializeAppDeployer(new DeployRestApiServersCommand(), new DeployOtherDatabasesCommand(1));

        appDeployer.deploy(appConfig);
        assertTrue(dbMgr.exists(dbName), "The content database should have been created by the REST API command");

        undeploySampleApp();
        assertFalse(dbMgr.exists(dbName), "The content database should have been deleted by the REST API command, and the database command shouldn't throw any error");
    }

    @Test
    public void emptyConfigDirWithContentDatabaseCommand() {
        DatabaseManager dbMgr = new DatabaseManager(manageClient);
        final String dbName = appConfig.getContentDatabaseName();

        appConfig.setConfigDir(new ConfigDir(new File("src/test/resources/sample-app/empty-ml-config")));

        initializeAppDeployer(new DeployRestApiServersCommand(), new DeployOtherDatabasesCommand(1));

        appDeployer.deploy(appConfig);
        assertTrue(dbMgr.exists(dbName), "The content database should have been created by the REST API command");

        undeploySampleApp();
        assertFalse(dbMgr.exists(dbName),
			"The content database should have been deleted by the content database command, even though a content-database.json file doesn't exist");
    }

    @Test
    public void emptyConfigDirWithNoContentDatabaseCommand() {
        DatabaseManager dbMgr = new DatabaseManager(manageClient);
        final String dbName = appConfig.getContentDatabaseName();
        final String modulesDbName = appConfig.getModulesDatabaseName();

        appConfig.setConfigDir(new ConfigDir(new File("src/test/resources/sample-app/empty-ml-config")));

        DeployRestApiServersCommand command = new DeployRestApiServersCommand();
        assertFalse(command.isDeleteContentDatabase(), "By default, this command shouldn't delete the content database");
        assertTrue(command.isDeleteModulesDatabase(), "By default, this command should delete the modules database");

        command.setDeleteContentDatabase(true);
        initializeAppDeployer(command);

        appDeployer.deploy(appConfig);
        assertTrue(dbMgr.exists(dbName), "The content database should have been created by the REST API command");
        assertTrue(dbMgr.exists(modulesDbName), "The modules database should have been created by the REST API command");

        undeploySampleApp();
        assertFalse(dbMgr.exists(dbName), "The content database should have been deleted by REST API command");
        assertFalse(dbMgr.exists(modulesDbName), "The modules database should have been deleted by REST API command");
    }
}
