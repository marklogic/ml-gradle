package com.marklogic.appdeployer.scaffold;

import java.io.File;

import com.marklogic.appdeployer.command.databases.DeployContentDatabasesCommand;
import org.junit.Test;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.databases.DeployTriggersDatabaseCommand;
import com.marklogic.appdeployer.command.restapis.DeployRestApiServersCommand;
import com.marklogic.appdeployer.command.security.DeployRolesCommand;
import com.marklogic.appdeployer.command.security.DeployUsersCommand;
import com.marklogic.mgmt.resource.databases.DatabaseManager;
import com.marklogic.mgmt.resource.security.RoleManager;
import com.marklogic.mgmt.resource.security.UserManager;

public class GenerateScaffoldTest extends AbstractAppDeployerTest {

    @Test
    public void generateScaffoldAndThenDeploy() {
        // Assume this is run out of the main directory, so default to "." and build out src/main etc.
        String path = "src/test/resources/scaffold-test";
        File dir = new File(path);
        dir.delete();
        dir.mkdirs();

        ScaffoldGenerator sg = new ScaffoldGenerator();
        sg.generateScaffold(path, appConfig);

        assertConfigFilesAreCreated(dir);
        assertModulesFilesAreCreated(dir);

        // Now try deploying the app
        appConfig.setConfigDir(new ConfigDir(new File(path, "src/main/ml-config")));
        appConfig.getModulePaths().clear();
        appConfig.getModulePaths().add(path + "/src/main/ml-modules");
        appConfig.setCreateTriggersDatabase(true);
        
        initializeAppDeployer(new DeployRestApiServersCommand(), new DeployContentDatabasesCommand(), new DeployTriggersDatabaseCommand(),
                new DeployUsersCommand(), new DeployRolesCommand(), buildLoadModulesCommand());
        appDeployer.deploy(appConfig);

        try {
            DatabaseManager dbMgr = new DatabaseManager(manageClient);
            assertTrue(dbMgr.exists(appConfig.getContentDatabaseName()));
            assertTrue(dbMgr.exists(appConfig.getTriggersDatabaseName()));

            assertTrue(new UserManager(manageClient).exists("sample-app-reader"));
			assertTrue(new UserManager(manageClient).exists("sample-app-writer"));
			assertTrue(new UserManager(manageClient).exists("sample-app-admin"));
            assertTrue(new RoleManager(manageClient).exists("sample-app-nobody"));
			assertTrue(new RoleManager(manageClient).exists("sample-app-reader"));
			assertTrue(new RoleManager(manageClient).exists("sample-app-writer"));
			assertTrue(new RoleManager(manageClient).exists("sample-app-internal"));
			assertTrue(new RoleManager(manageClient).exists("sample-app-admin"));
        } finally {
            undeploySampleApp();
        }
    }

    private void assertConfigFilesAreCreated(File dir) {
        File configDir = new File(dir, "src/main/ml-config");
        assertTrue(configDir.exists());
        assertTrue(new File(configDir, "rest-api.json").exists());
        assertTrue(new File(configDir, "databases/content-database.json").exists());
    }

    private void assertModulesFilesAreCreated(File dir) {
        File modulesDir = new File(dir, "src/main/ml-modules");
        assertTrue(modulesDir.exists());
        assertTrue(new File(modulesDir, "rest-properties.json").exists());
        assertTrue(new File(modulesDir, "options/sample-app-options.xml").exists());
    }
}
