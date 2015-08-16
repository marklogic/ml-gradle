package com.rjrudin.marklogic.appdeployer.scaffold;

import java.io.File;

import org.junit.Test;

import com.rjrudin.marklogic.appdeployer.AbstractAppDeployerTest;
import com.rjrudin.marklogic.appdeployer.ConfigDir;
import com.rjrudin.marklogic.appdeployer.command.databases.DeployTriggersDatabaseCommand;
import com.rjrudin.marklogic.appdeployer.command.modules.LoadModulesCommand;
import com.rjrudin.marklogic.appdeployer.command.restapis.DeployRestApiServersCommand;
import com.rjrudin.marklogic.appdeployer.command.security.DeployRolesCommand;
import com.rjrudin.marklogic.appdeployer.command.security.DeployUsersCommand;
import com.rjrudin.marklogic.appdeployer.scaffold.ScaffoldGenerator;
import com.rjrudin.marklogic.mgmt.databases.DatabaseManager;
import com.rjrudin.marklogic.mgmt.security.RoleManager;
import com.rjrudin.marklogic.mgmt.security.UserManager;

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

        initializeAppDeployer(new DeployRestApiServersCommand(), new DeployTriggersDatabaseCommand(),
                new DeployUsersCommand(), new DeployRolesCommand(), new LoadModulesCommand());
        appDeployer.deploy(appConfig);

        try {
            DatabaseManager dbMgr = new DatabaseManager(manageClient);
            assertTrue(dbMgr.exists(appConfig.getContentDatabaseName()));
            assertTrue(dbMgr.exists(appConfig.getTriggersDatabaseName()));

            assertTrue(new UserManager(manageClient).exists("sample-app-user"));
            assertTrue(new RoleManager(manageClient).exists("sample-app-role"));
        } finally {
            undeploySampleApp();
        }
    }

    private void assertConfigFilesAreCreated(File dir) {
        File configDir = new File(dir, "src/main/ml-config");
        assertTrue(configDir.exists());
        assertTrue(new File(configDir, "rest-api.json").exists());
        assertTrue(new File(configDir, "databases/content-database.json").exists());
        assertTrue(new File(configDir, "security/roles/sample-app-role.json").exists());
        assertTrue(new File(configDir, "security/users/sample-app-user.json").exists());
    }

    private void assertModulesFilesAreCreated(File dir) {
        File modulesDir = new File(dir, "src/main/ml-modules");
        assertTrue(modulesDir.exists());
        assertTrue(new File(modulesDir, "rest-properties.json").exists());
        assertTrue(new File(modulesDir, "options/sample-app-options.xml").exists());
    }
}
