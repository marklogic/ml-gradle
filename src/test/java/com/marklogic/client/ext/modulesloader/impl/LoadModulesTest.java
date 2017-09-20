package com.marklogic.client.ext.modulesloader.impl;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.ext.AbstractIntegrationTest;
import org.junit.Test;
import org.springframework.core.io.Resource;

import java.util.Set;

public class LoadModulesTest extends AbstractIntegrationTest {

	private DatabaseClient modulesClient;

	@Test
	public void test() {
		client = newClient("Modules");
		client.newServerEval().xquery("cts:uris((), (), cts:true-query()) ! xdmp:document-delete(.)").eval();
		modulesClient = client;

		/**
		 * Odd - the Client REST API doesn't allow for loading namespaces when the DatabaseClient has a database
		 * specified, so we construct a DatabaseClient without a database and assume we get "Documents".
		 */
		String currentDatabase = clientConfig.getDatabase();
		clientConfig.setDatabase(null);
		client = configuredDatabaseClientFactory.newDatabaseClient(clientConfig);
		clientConfig.setDatabase(currentDatabase);

		DefaultModulesLoader modulesLoader = new DefaultModulesLoader(new AssetFileLoader(modulesClient));
		modulesLoader.setModulesManager(null);

		String dir = "src/test/resources/sample-base-dir";
		Set<Resource> files = modulesLoader.loadModules(dir, new DefaultModulesFinder(), client);
		assertEquals(21, files.size());
		assertModuleExists("/ext/module1.xqy");
		assertModuleExists("/ext/module1.sjs");
		assertModuleExists("/ext/lib/module2.xqy");
		assertModuleExists("/ext/lib/module2.sjs");
		assertModuleExists("/include-module.xqy");
		assertModuleExists("/include-module.sjs");
		assertModuleExists("/module3.xqy");
		assertModuleExists("/module3.sjs");
		assertModuleExists("/lib/module4.xqy");
		assertModuleExists("/lib/module4.sjs");
		final int initialModuleCount = getUriCountInModulesDatabase();

		// Load again with a modules manager, make sure all files are loaded but no new docs in the modules database
		PropertiesModuleManager moduleManager = new PropertiesModuleManager();
		moduleManager.deletePropertiesFile();
		modulesLoader.setAssetFileLoader(new AssetFileLoader(modulesClient, moduleManager));
		modulesLoader.setModulesManager(moduleManager);
		files = modulesLoader.loadModules(dir, new DefaultModulesFinder(), client);
		assertEquals("All files should have been loaded since a ModulesManager wasn't used on the first load", 21, files.size());
		assertEquals("No new modules should have been created", initialModuleCount, getUriCountInModulesDatabase());

		// Load again; this time, no files should have been loaded
		files = modulesLoader.loadModules(dir, new DefaultModulesFinder(), client);
		assertEquals("No files should have been loaded since none were new or modified", 0, files.size());
		assertEquals("Module count shouldn't have changed either", initialModuleCount, getUriCountInModulesDatabase());
	}

	private int getUriCountInModulesDatabase() {
		return Integer.parseInt(modulesClient.newServerEval().xquery("count(cts:uris((), (), cts:true-query()))").evalAs(String.class));
	}

	private void assertModuleExists(String uri) {
		assertEquals("true",
			modulesClient.newServerEval().xquery(String.format("fn:doc-available('%s')", uri)).evalAs(String.class)
		);
	}
}
