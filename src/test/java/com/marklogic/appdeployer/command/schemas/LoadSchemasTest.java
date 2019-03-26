package com.marklogic.appdeployer.command.schemas;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.command.Command;
import com.marklogic.appdeployer.command.databases.DeployContentDatabasesCommand;
import com.marklogic.appdeployer.command.databases.DeployOtherDatabasesCommand;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.GenericDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.io.FileFilter;

public class LoadSchemasTest extends AbstractAppDeployerTest {

	@After
	public void cleanup() {
		undeploySampleApp();
	}

	@Test
	public void databaseSpecificPaths() {
		initializeAppDeployer(new DeployOtherDatabasesCommand(), newCommand());

		appConfig.getFirstConfigDir().setBaseDir(new File("src/test/resources/sample-app/multiple-schema-databases/ml-config"));
		appConfig.setSchemasDatabaseName("sample-app-schemas1");
		appConfig.getSchemaPaths().clear();
		appConfig.getSchemaPaths().add("src/test/resources/sample-app/multiple-schema-databases/ml-schemas");

		deploySampleApp();

		DatabaseClient client = appConfig.newSchemasDatabaseClient();
		GenericDocumentManager mgr = client.newDocumentManager();
		assertNotNull(mgr.exists("/default-schema.xsd"));
		assertNotNull(mgr.exists("/schema1.xsd"));
		assertNull(mgr.exists("/schema2.xsd"));
		client.release();

		client = appConfig.newAppServicesDatabaseClient("sample-app-schemas2");
		mgr = client.newDocumentManager();
		assertNull(mgr.exists("/default-schema.xsd"));
		assertNull(mgr.exists("/schema1.xsd"));
		assertNotNull(mgr.exists("/schema2.xsd"));
		client.release();
	}

	@Test
	public void testSchemaLoading() {
		initializeAppDeployer(new DeployOtherDatabasesCommand(),
			new DeployContentDatabasesCommand(1), newCommand());
		appDeployer.deploy(appConfig);

		DatabaseClient client = appConfig.newSchemasDatabaseClient();

		GenericDocumentManager docMgr = client.newDocumentManager();

		assertNull("Rules document loaded", docMgr.exists("notExists"));
		assertNotNull("Rules document loaded", docMgr.exists("/my.rules").getUri());
		assertNotNull("XSD document loaded", docMgr.exists("/x.xsd").getUri());
		assertNull(docMgr.exists("/.do-not-load"));
		assertNull(docMgr.exists(".do-not-load"));
	}

	@Test
	public void testCustomSchemasPathWithCustomFileFilter() {
		initializeAppDeployer(new DeployOtherDatabasesCommand(), new DeployContentDatabasesCommand(1), newCommand());

		appConfig.getSchemaPaths().clear();
		appConfig.getSchemaPaths().add("src/test/resources/schemas-marklogic9");
		appConfig.setSchemasFileFilter(new CustomFileFilter());
		appConfig.setTdeValidationEnabled(false);
		appDeployer.deploy(appConfig);

		DatabaseClient client = appConfig.newSchemasDatabaseClient();

		GenericDocumentManager docMgr = client.newDocumentManager();

		assertNotNull("TDEXML document loaded", docMgr.exists("/x.tdex").getUri());
		assertNotNull("TDEJSON document loaded", docMgr.exists("/x.tdej").getUri());
		assertNull(docMgr.exists("/to-be-ignored/test.xml"));
		assertNull(docMgr.exists("to-be-ignored/test.xml"));

		for (String uri : new String[]{"/x.tdex", "/x.tdej"}) {
			DocumentMetadataHandle h = docMgr.readMetadata(uri, new DocumentMetadataHandle());
			assertEquals("Files ending in tdex and tdej go into a special collection", "http://marklogic.com/xdmp/tde",
				h.getCollections().iterator().next());
		}
	}

	@Test
	public void nullSchemaPath() {
		initializeAppDeployer(newCommand());
		appConfig.setSchemaPaths(null);
		deploySampleApp();
		logger.info("Verifies that no error occurs when the schemas path is null");
	}

	@Test
	public void tdeValidationEnabled() {
		initializeAppDeployer(new DeployOtherDatabasesCommand(), new DeployContentDatabasesCommand(1), newCommand());
		appConfig.getFirstConfigDir().setBaseDir(new File("src/test/resources/sample-app/tde-validation"));
		try {
			deploySampleApp();
			fail("The deploy should have failed because of a bad TDE template");
		} catch (Exception ex) {
			String message = ex.getCause().getMessage();
			assertTrue(message.startsWith("TDE template failed validation"));
			assertTrue(message.contains("TDE-REPEATEDCOLUMN"));
		}
	}

	@Test
	public void multipleSchemaPaths() {
		File projectDir = new File("src/test/resources/schemas-project");

		initializeAppConfig(projectDir);
		appConfig.getSchemaPaths().add(new File(projectDir, "src/main/more-schemas").getAbsolutePath());

		initializeAppDeployer(new DeployContentDatabasesCommand(1), new DeployOtherDatabasesCommand(),
			new LoadSchemasCommand());
		deploySampleApp();

		DatabaseClient client = appConfig.newSchemasDatabaseClient();
		GenericDocumentManager docMgr = client.newDocumentManager();
		assertNotNull(docMgr.exists("/tde/template1.json"));
		assertNotNull(docMgr.exists("/tde/template2.json"));

		assertTrue(docMgr.readMetadata("/tde/template1.json", new DocumentMetadataHandle()).getCollections().contains("http://marklogic.com/xdmp/tde"));
		assertTrue(docMgr.readMetadata("/tde/template2.json", new DocumentMetadataHandle()).getCollections().contains("http://marklogic.com/xdmp/tde"));
	}

	private Command newCommand() {
		return new LoadSchemasCommand();
	}

}

class CustomFileFilter implements FileFilter {
	@Override
	public boolean accept(File pathname) {
		return !(pathname.isDirectory() && "to-be-ignored".equals(pathname.getName()));
	}
}
