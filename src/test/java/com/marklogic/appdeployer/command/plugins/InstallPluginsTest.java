package com.marklogic.appdeployer.command.plugins;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.command.databases.DeployOtherDatabasesCommand;
import com.marklogic.appdeployer.command.restapis.DeployRestApiServersCommand;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.GenericDocumentManager;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.mgmt.util.ObjectMapperFactory;
import org.junit.After;
import org.junit.Test;

import java.io.File;

public class InstallPluginsTest extends AbstractAppDeployerTest {

	@After
	public void teardown() {
		initializeAppDeployer(new DeployOtherDatabasesCommand(1), new InstallPluginsCommand(), new DeployRestApiServersCommand());
		undeploySampleApp();
	}

	@Test
	public void test() {
		final File projectDir = new File("src/test/resources/plugin-project");
		deleteMadeFiles(projectDir);

		initializeAppConfig(projectDir);

		initializeAppDeployer(new DeployOtherDatabasesCommand(1), new InstallPluginsCommand(), new DeployRestApiServersCommand());
		appConfig.getPluginConfig().setDatabaseName(appConfig.getContentDatabaseName());

		deploySampleApp();

		DatabaseClient contentClient = appConfig.newAppServicesDatabaseClient(appConfig.getContentDatabaseName());
		assertNotNull("The plugin zip should have been written to the content database so that it can be installed",
			contentClient.newDocumentManager().exists("/com.marklogic/plugins/varianceplugin.zip"));

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

		assertTrue(invokePlugin(contentClient).startsWith("22.22"));

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
