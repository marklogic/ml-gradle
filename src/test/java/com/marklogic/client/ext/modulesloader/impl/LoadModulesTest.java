package com.marklogic.client.ext.modulesloader.impl;

import com.marklogic.client.ext.AbstractIntegrationTest;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.util.Set;

public class LoadModulesTest extends AbstractIntegrationTest {

	private DatabaseClient modulesClient;

	@After
	public void teardown() {
		if (modulesClient != null) {
			modulesClient.release();
		}
	}

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

		// Load without a ModulesManager first
		File dir = new File("src/test/resources/sample-base-dir");
		Set<File> files = modulesLoader.loadModules(dir, new DefaultModulesFinder(), client);
		assertEquals(13, files.size());
		assertModuleExists("/ext/module1.xqy");
		assertModuleExists("/ext/lib/module2.xqy");
		assertModuleExists("/include-module.xqy");
		assertModuleExists("/module3.xqy");
		assertModuleExists("/lib/module4.xqy");
		final int initialModuleCount = getUriCountInModulesDatabase();

		// Use a modules manager, but set the timestamp in the future first
		PropertiesModuleManager moduleManager = new PropertiesModuleManager();
		modulesLoader.setAssetFileLoader(new AssetFileLoader(modulesClient, moduleManager));
		modulesLoader.setModulesManager(moduleManager);
		moduleManager.setMinimumFileTimestampToLoad(System.currentTimeMillis() + 10000);
		files = modulesLoader.loadModules(dir, new DefaultModulesFinder(), client);
		assertEquals("No files should have been loaded since the minimum last-modified timestamp is in the future", 0, files.size());

		// Remove the timestamp minimum, all the modules should be loaded
		moduleManager.deletePropertiesFile();
		moduleManager.setMinimumFileTimestampToLoad(0);
		files = modulesLoader.loadModules(dir, new DefaultModulesFinder(), client);
		assertEquals("All files should have been loaded since a ModulesManager wasn't used on the first load", 13, files.size());
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
