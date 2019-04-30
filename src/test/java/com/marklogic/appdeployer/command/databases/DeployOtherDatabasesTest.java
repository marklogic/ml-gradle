package com.marklogic.appdeployer.command.databases;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.mgmt.resource.databases.DatabaseManager;
import org.junit.Test;

import java.io.File;
import java.util.regex.Pattern;

public class DeployOtherDatabasesTest extends AbstractAppDeployerTest {

	@Test
	public void dontCreateForests() {
		ConfigDir configDir = appConfig.getFirstConfigDir();
		configDir.setBaseDir(new File("src/test/resources/sample-app/lots-of-databases"));

		appConfig.setResourceFilenamesIncludePattern(Pattern.compile("other-schemas-database.*"));
		appConfig.setCreateForests(false);

		initializeAppDeployer(new DeployOtherDatabasesCommand());

		final String dbName = "other-sample-app-schemas";
		DatabaseManager dbMgr = new DatabaseManager(manageClient);

		try {
			appDeployer.deploy(appConfig);
			assertTrue(dbMgr.exists(dbName));
			assertTrue("No forests should have been created for the database", dbMgr.getForestIds(dbName).isEmpty());
		} finally {
			undeploySampleApp();
			assertFalse(dbMgr.exists(dbName));
		}
	}

    @Test
    public void test() {
        ConfigDir configDir = appConfig.getFirstConfigDir();
        configDir.setBaseDir(new File("src/test/resources/sample-app/lots-of-databases"));
        configDir.getContentDatabaseFiles().add(new File(configDir.getBaseDir(), "other-database-files/more-content-db-config.json"));

        appConfig.setContentForestsPerHost(2);
        appConfig.getForestCounts().put("other-sample-app-content", 2);
        appConfig.getForestCounts().put("other-sample-app-schemas", 3);
        appConfig.setResourceFilenamesToIgnore("ignored-database.json");

        // Speed up this test and ensure that all the forests still get created correctly
        appConfig.getCmaConfig().setDeployForests(true);

        initializeAppDeployer(new DeployOtherDatabasesCommand());

        DatabaseManager dbMgr = new DatabaseManager(manageClient);

        String[] dbNames = new String[] { "sample-app-content", "sample-app-triggers", "sample-app-schemas",
                "other-sample-app-content", "other-sample-app-triggers", "other-sample-app-schemas" };
        try {
            appDeployer.deploy(appConfig);

            for (String name : dbNames) {
                assertTrue("Expected to find database: " + name, dbMgr.exists(name));
            }
            assertFalse("ignored-database.json should have been ignored", dbMgr.exists("ignored-content"));

            assertEquals("The main content database should have 2 forests, as set in the command", 2,
                    dbMgr.getForestIds("sample-app-content").size());
            assertEquals("AppConfig is configured for other-sample-app-content to have 2 forests instead of 1", 2,
                    dbMgr.getForestIds("other-sample-app-content").size());
            assertEquals("other-sample-app-schemas is configured to have 3 forests", 3,
                    dbMgr.getForestIds("other-sample-app-schemas").size());
            assertEquals("other-sample-app-triggers should have the default of 1 forest", 1,
                    dbMgr.getForestIds("other-sample-app-triggers").size());
        } finally {
            undeploySampleApp();

            for (String name : dbNames) {
                assertFalse("Expected to not find database: " + name, dbMgr.exists(name));
            }
        }
    }
}
