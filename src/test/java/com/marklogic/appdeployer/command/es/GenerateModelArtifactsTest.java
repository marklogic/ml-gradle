package com.marklogic.appdeployer.command.es;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.databases.DeployContentDatabasesCommand;
import com.marklogic.appdeployer.command.databases.DeploySchemasDatabaseCommand;
import com.marklogic.appdeployer.command.modules.LoadModulesCommand;
import com.marklogic.appdeployer.command.restapis.DeployRestApiServersCommand;
import com.marklogic.appdeployer.command.schemas.LoadSchemasCommand;
import org.junit.After;
import org.junit.Test;

import java.io.File;

public class GenerateModelArtifactsTest extends AbstractAppDeployerTest {

	@After
	public void tearDown() {
		initializeAppDeployer(new DeployContentDatabasesCommand(), new DeploySchemasDatabaseCommand(),
			new DeployRestApiServersCommand());
		undeploySampleApp();
	}

	@Test
	public void test() {
		String projectPath = "src/test/resources/entity-services-project";
		File srcDir = new File(projectPath, "src");
		if (srcDir.exists()) {
			srcDir.delete();
		}
		appConfig.setConfigDir(new ConfigDir(new File(projectPath + "/src/main/ml-config")));
		appConfig.setModelsPath(projectPath + "/data/entity-services");
		appConfig.getModulePaths().clear();
		appConfig.getModulePaths().add(projectPath + "/src/main/ml-modules");
		appConfig.setSchemasPath(projectPath + "/src/main/ml-schemas");

		initializeAppDeployer(new DeployContentDatabasesCommand(1), new DeploySchemasDatabaseCommand(),
			new DeployRestApiServersCommand(), new GenerateModelArtifactsCommand());
		deploySampleApp();

		assertTrue(new File(projectPath, "src/main/ml-modules/ext/entity-services/Race-0.0.1.xqy").exists());
		assertTrue(new File(projectPath, "src/main/ml-modules/options/Race.xml").exists());
		assertTrue(new File(projectPath, "src/main/ml-schemas/Race-0.0.1.xsd").exists());
		assertTrue(new File(projectPath, "src/main/ml-schemas/Race-0.0.1.tdex").exists());
		assertTrue(new File(projectPath, "src/main/ml-config/databases/content-database.json").exists());
		assertTrue("A schemas db file needs to be created since the ES content-database.json file refers to one",
			new File(projectPath, "src/main/ml-config/databases/schemas-database.json").exists());

		deploySampleApp();

		assertTrue(new File(projectPath, "src/main/ml-modules/ext/entity-services/Race-0.0.1-GENERATED.xqy").exists());
		assertTrue(new File(projectPath, "src/main/ml-modules/options/Race-GENERATED.xml").exists());
		assertTrue(new File(projectPath, "src/main/ml-config/databases/content-database-GENERATED.json").exists());
		assertTrue(new File(projectPath, "src/main/ml-schemas/Race-0.0.1-GENERATED.xsd").exists());
		assertTrue(new File(projectPath, "src/main/ml-schemas/Race-0.0.1-GENERATED.tdex").exists());

		// Make sure none of these files break when they're deployed
		initializeAppDeployer(new DeployContentDatabasesCommand(), new DeploySchemasDatabaseCommand(),
			new LoadSchemasCommand(), new LoadModulesCommand());
		deploySampleApp();
	}
}
