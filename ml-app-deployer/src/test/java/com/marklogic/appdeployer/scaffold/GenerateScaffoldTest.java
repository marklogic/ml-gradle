/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.scaffold;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.databases.DeployOtherDatabasesCommand;
import com.marklogic.appdeployer.command.restapis.DeployRestApiServersCommand;
import com.marklogic.appdeployer.command.security.DeployRolesCommand;
import com.marklogic.appdeployer.command.security.DeployUsersCommand;
import com.marklogic.mgmt.resource.databases.DatabaseManager;
import com.marklogic.mgmt.resource.security.RoleManager;
import com.marklogic.mgmt.resource.security.UserManager;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class GenerateScaffoldTest extends AbstractAppDeployerTest {

	// Assume this is run out of the main directory, so default to "." and build out src/main etc.
	final static String ROOT_PATH = "src/test/resources/scaffold-test";
	final static File dir = new File(ROOT_PATH);
	final static File configDir = new File(dir, "src/main/ml-config");
	final static File modulesDir = new File(dir, "src/main/ml-modules");

	@BeforeEach
	public void beforeEach() throws IOException {
		if (dir.exists()) {
			FileUtils.deleteDirectory(dir);
		}
		assertTrue(dir.mkdirs());
	}

    @Test
    public void generateScaffoldWithDefaultsAndThenDeploy() {
		ScaffoldGenerator.AppInputs appInputs = new ScaffoldGenerator.AppInputs(SAMPLE_APP_NAME, true, true);
		new ScaffoldGenerator().generateScaffold(ROOT_PATH, appInputs);

        assertConfigFilesAreCreated( true);
        assertModulesFilesAreCreated(true, true);

		deployAppUsingAppConfig();
        try {
            DatabaseManager dbMgr = new DatabaseManager(manageClient);
            assertTrue(dbMgr.exists(SAMPLE_APP_NAME + "-content"));
			assertTrue(dbMgr.exists(SAMPLE_APP_NAME + "-schemas"));
			assertSecurity(true);
        } finally {
            undeploySampleApp();
        }
    }

	@Test
	public void generateScaffoldWithChoicesSetToFalseAndThenDeploy() {
		ScaffoldGenerator.AppInputs appInputs = new ScaffoldGenerator.AppInputs(SAMPLE_APP_NAME, false, false);
		new ScaffoldGenerator().generateScaffold(ROOT_PATH, appInputs);

		assertConfigFilesAreCreated(false);
		assertModulesFilesAreCreated(false, false);

		deployAppUsingAppConfig();
		try {
			DatabaseManager dbMgr = new DatabaseManager(manageClient);
			assertTrue(dbMgr.exists(appConfig.getContentDatabaseName()));
			assertSecurity(false);
		} finally {
			undeploySampleApp();
		}
	}

	@Test
	public void generateScaffoldWithDatabaseNoSecurityAndThenDeploy() {
		ScaffoldGenerator.AppInputs appInputs = new ScaffoldGenerator.AppInputs(SAMPLE_APP_NAME, true, false);
		new ScaffoldGenerator().generateScaffold(ROOT_PATH, appInputs);

		assertConfigFilesAreCreated(true);
		assertModulesFilesAreCreated(true, true);

		deployAppUsingAppConfig();
		try {
			DatabaseManager dbMgr = new DatabaseManager(manageClient);
			assertTrue(dbMgr.exists(SAMPLE_APP_NAME + "-content"));
			assertTrue(dbMgr.exists(SAMPLE_APP_NAME + "-schemas"));
			assertSecurity(false);
		} finally {
			undeploySampleApp();
		}
	}

	private void deployAppUsingAppConfig() {
		appConfig.setConfigDir(new ConfigDir(new File(ROOT_PATH, "src/main/ml-config")));
		appConfig.getModulePaths().clear();
		appConfig.getModulePaths().add(ROOT_PATH + "/src/main/ml-modules");
		initializeAppDeployer(new DeployRestApiServersCommand(), new DeployOtherDatabasesCommand(),
			new DeployUsersCommand(), new DeployRolesCommand(), buildLoadModulesCommand());
		appDeployer.deploy(appConfig);
	}

    private void assertConfigFilesAreCreated(Boolean restApiExist) {
        assertTrue(configDir.exists());
		assertEquals(restApiExist, new File(configDir, "rest-api.json").exists());
		assertTrue(new File(configDir, "databases/content-database.json").exists());
		assertTrue(new File(configDir, "databases/schemas-database.json").exists());
    }

    private void assertModulesFilesAreCreated(Boolean restPropertiesExist, Boolean sampleOptionsExist) {
        assertTrue(modulesDir.exists());
		assertEquals(restPropertiesExist, new File(modulesDir, "rest-properties.json").exists());
		assertEquals(sampleOptionsExist, new File(modulesDir, "options/sample-app-options.xml").exists());
    }

	private void assertSecurity(Boolean shouldExist) {
		assertEquals(shouldExist, new UserManager(manageClient).exists("sample-app-reader"));
		assertEquals(shouldExist, new UserManager(manageClient).exists("sample-app-writer"));
		assertEquals(shouldExist, new UserManager(manageClient).exists("sample-app-admin"));
		assertEquals(shouldExist, new RoleManager(manageClient).exists("sample-app-nobody"));
		assertEquals(shouldExist, new RoleManager(manageClient).exists("sample-app-reader"));
		assertEquals(shouldExist, new RoleManager(manageClient).exists("sample-app-writer"));
		assertEquals(shouldExist, new RoleManager(manageClient).exists("sample-app-internal"));
		assertEquals(shouldExist, new RoleManager(manageClient).exists("sample-app-admin"));
		if (shouldExist) {
			assertTrue(new RoleManager(manageClient).getPropertiesAsXmlString("sample-app-reader").contains("http://marklogic.com/xdmp/privileges/rest-reader"));
			assertTrue(new RoleManager(manageClient).getPropertiesAsXmlString("sample-app-writer").contains("http://marklogic.com/xdmp/privileges/rest-writer"));
		}
	}
}
