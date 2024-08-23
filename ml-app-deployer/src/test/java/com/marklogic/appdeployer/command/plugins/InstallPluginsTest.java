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
package com.marklogic.appdeployer.command.plugins;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.command.databases.DeployOtherDatabasesCommand;
import com.marklogic.appdeployer.command.restapis.DeployRestApiServersCommand;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.document.GenericDocumentManager;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.mgmt.util.ObjectMapperFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class InstallPluginsTest extends AbstractAppDeployerTest {

	@AfterEach
	public void teardown() {
		initializeAppDeployer(new DeployOtherDatabasesCommand(1), new InstallPluginsCommand(), new DeployRestApiServersCommand());
		undeploySampleApp();
	}

	@Disabled("The makefile needs updating")
	@Test
	public void test() {
		final File projectDir = new File("src/test/resources/plugin-project");
		deleteMadeFiles(projectDir);

		initializeAppConfig(projectDir);

		initializeAppDeployer(new DeployOtherDatabasesCommand(1), new InstallPluginsCommand(), new DeployRestApiServersCommand());
		appConfig.getPluginConfig().setDatabaseName(appConfig.getContentDatabaseName());

		deploySampleApp();

		DatabaseClient contentClient = appConfig.newDatabaseClient();
		assertNotNull(contentClient.newDocumentManager().exists("/com.marklogic/plugins/varianceplugin.zip"),
			"The plugin zip should have been written to the content database so that it can be installed");

		// Make sure everything is in Extensions as expected
		DatabaseClient extensionsClient = appConfig.newAppServicesDatabaseClient("Extensions");
		GenericDocumentManager mgr = extensionsClient.newDocumentManager();
		assertNotNull(mgr.exists("/native/scope.xml"));
		assertTrue(mgr.readMetadata("/native/varianceplugin/libvarianceplugin.dylib", new DocumentMetadataHandle())
			.getCollections().contains("http://marklogic.com/extension/native-plugin"));
		assertTrue(mgr.readMetadata("/native/varianceplugin/manifest.xml", new DocumentMetadataHandle())
			.getCollections().contains("http://marklogic.com/extension/native-plugin"));

		// Insert some data and verify that the plugin can be invoked
		JSONDocumentManager jsonDocumentManager = contentClient.newJSONDocumentManager();
		ObjectNode object = ObjectMapperFactory.getObjectMapper().createObjectNode();
		object.put("amount", 20);
		jsonDocumentManager.write("test1.json", new JacksonHandle(object));
		object.put("amount", 30);
		jsonDocumentManager.write("test2.json", new JacksonHandle(object));
		object.put("amount", 30);
		jsonDocumentManager.write("test3.json", new JacksonHandle(object));

		try {
			String pluginResponse = invokePlugin(contentClient);
			assertTrue(pluginResponse.startsWith("22.22"), "Unexpected plugin response: " + pluginResponse);
		} catch (FailedRequestException ex) {
			String message = ex.getMessage();
			if (message.contains("invalid ELF header")) {
				logger.warn("Could not verify that the plugin works as the platform that the plugin was built on does " +
					"not match that of the platform running MarkLogic; error: " + message);
			} else {
				throw ex;
			}
		}

		// Now uninstall the plugin and make sure the plugin can't be invoked
		initializeAppDeployer(new InstallPluginsCommand());

		deleteMadeFiles(projectDir);
		undeploySampleApp();

		try {
			String result = invokePlugin(contentClient);
			fail("Invoking the plugin should have failed because it was uninstalled; result: " + result);
		} catch (Exception ex) {
			logger.info("Caught expected exception, as plugin was uninstalled: " + ex.getMessage());
		}
	}

	private void deleteMadeFiles(File projectDir) {
		File pluginDir = new File(projectDir, "src/main/ml-plugins/variance");
		if (pluginDir != null && pluginDir.exists()) {
			new File(pluginDir, "manifest.xml").delete();
			new File(pluginDir, "libvarianceplugin.dylib").delete();
			new File(pluginDir, "VariancePlugin.o").delete();
			new File(pluginDir, "varianceplugin.zip").delete();
		}
	}

	private String invokePlugin(DatabaseClient contentClient) {
		return contentClient.newServerEval()
			.xquery("cts:aggregate('native/varianceplugin', 'variance', cts:element-reference(xs:QName('amount'), 'type=double'))")
			.evalAs(String.class);
	}
}
