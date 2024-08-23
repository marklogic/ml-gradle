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
package com.marklogic.appdeployer.command.es;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.databases.DeployOtherDatabasesCommand;
import com.marklogic.appdeployer.command.restapis.DeployRestApiServersCommand;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.io.StringHandle;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GenerateModelArtifactsTest extends AbstractAppDeployerTest {

	@AfterEach
	public void tearDown() {
		undeploySampleApp();
	}

	@Test
	public void test() throws IOException {
		String projectPath = "src/test/resources/entity-services-project";
		File srcDir = new File(projectPath, "src");
		if (srcDir.exists()) {
			FileUtils.cleanDirectory(srcDir);
		}

		appConfig.setConfigDir(new ConfigDir(new File(projectPath + "/src/main/ml-config")));
		appConfig.setModelsPath(projectPath + "/data/entity-services");
		appConfig.getModulePaths().clear();
		appConfig.getModulePaths().add(projectPath + "/src/main/ml-modules");
		appConfig.getSchemaPaths().clear();
		appConfig.getSchemaPaths().add(projectPath + "/src/main/ml-schemas");
		appConfig.setModelsDatabase(appConfig.getContentDatabaseName());

		initializeAppDeployer(new DeployOtherDatabasesCommand(1),
			new DeployRestApiServersCommand(), new GenerateModelArtifactsCommand());
		deploySampleApp();

		assertTrue(new File(projectPath, "src/main/ml-modules/ext/entity-services/Race-0.0.1.xqy").exists());
		assertTrue(new File(projectPath, "src/main/ml-modules/options/Race.xml").exists());
		assertTrue(new File(projectPath, "src/main/ml-schemas/Race-0.0.1.xsd").exists());
		assertTrue(new File(projectPath, "src/main/ml-schemas/tde/Race-0.0.1.tdex").exists());
		assertTrue(new File(projectPath, "src/main/ml-config/databases/content-database.json").exists());
		assertTrue(new File(projectPath, "src/main/ml-config/databases/schemas-database.json").exists(),
			"A schemas db file needs to be created since the ES content-database.json file refers to one");

		// Verify the model was loaded into the database
		DatabaseClient modelsClient = appConfig.newAppServicesDatabaseClient(appConfig.getModelsDatabase());
		try {
			String raceModel = modelsClient.newDocumentManager().read("/marklogic.com/entity-services/models/race.json").nextContent(new StringHandle()).get();
			assertTrue(raceModel.contains("This schema represents a Runner"),
				"Simple smoke test to make sure the race model came back");
		} finally {
			modelsClient.release();
		}

		deploySampleApp();

		// These shouldn't exist because the content is the same
		assertFalse(new File(projectPath, "src/main/ml-modules/ext/entity-services/Race-0.0.1.xqy.GENERATED").exists());
		assertFalse(new File(projectPath, "src/main/ml-modules/options/Race.xml.GENERATED").exists());
		assertFalse(new File(projectPath, "src/main/ml-config/databases/content-database.json.GENERATED").exists());
		assertFalse(new File(projectPath, "src/main/ml-schemas/Race-0.0.1.xsd.GENERATED").exists());
		assertFalse(new File(projectPath, "src/main/ml-schemas/Race-0.0.1.tdex.GENERATED").exists());
	}
}
