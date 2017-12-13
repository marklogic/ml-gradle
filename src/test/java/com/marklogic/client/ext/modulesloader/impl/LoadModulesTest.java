package com.marklogic.client.ext.modulesloader.impl;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.ext.AbstractIntegrationTest;
import com.marklogic.client.ext.tokenreplacer.DefaultTokenReplacer;
import com.marklogic.client.ext.tokenreplacer.TokenReplacer;
import com.marklogic.client.io.BytesHandle;
import com.marklogic.client.io.StringHandle;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.Resource;

import java.nio.file.Paths;
import java.util.Properties;
import java.util.Set;

public class LoadModulesTest extends AbstractIntegrationTest {

	private DatabaseClient modulesClient;
	private DefaultModulesLoader modulesLoader;

	@Before
	public void setup() {
		client = newClient("Modules");
		client.newServerEval().xquery("cts:uris((), (), cts:true-query()) ! xdmp:document-delete(.)").eval();
		modulesClient = client;
		assertEquals("No new modules should have been created", 0, getUriCountInModulesDatabase());

		/**
		 * Odd - the Client REST API doesn't allow for loading namespaces when the DatabaseClient has a database
		 * specified, so we construct a DatabaseClient without a database and assume we get "Documents".
		 */
		String currentDatabase = clientConfig.getDatabase();
		clientConfig.setDatabase(null);
		client = configuredDatabaseClientFactory.newDatabaseClient(clientConfig);
		clientConfig.setDatabase(currentDatabase);

		modulesLoader = new DefaultModulesLoader(new AssetFileLoader(modulesClient));
		modulesLoader.setModulesManager(null);
	}

	/**
	 * This test is a little brittle because it assumes the URI of options/services/transforms that are loaded
	 * into the Modules database.
	 */
	@Test
	public void replaceTokens() {
		String dir = Paths.get("src", "test", "resources", "token-replace").toString();

		DefaultTokenReplacer tokenReplacer = new DefaultTokenReplacer();
		Properties props = new Properties();
		props.setProperty("%%REPLACEME%%", "hello-world");
		tokenReplacer.setProperties(props);
		modulesLoader.setTokenReplacer(tokenReplacer);

		modulesLoader.loadModules(dir, new DefaultModulesFinder(), client);

		String optionsXml = modulesClient.newXMLDocumentManager().read(
			"/Default/App-Services/rest-api/options/sample-options.xml", new StringHandle()).get();
		assertTrue(optionsXml.contains("fn:collection('hello-world')"));

		String serviceText = new String(modulesClient.newDocumentManager().read(
			"/marklogic.rest.resource/sample/assets/resource.xqy", new BytesHandle()).get());
		assertTrue(serviceText.contains("xdmp:log(\"hello-world called\")"));

		String transformText = new String(modulesClient.newDocumentManager().read(
			"/marklogic.rest.transform/xquery-transform/assets/transform.xqy", new BytesHandle()).get());
		assertTrue(transformText.contains("xdmp:log(\"hello-world\")"));
	}

	@Test
	public void test() {
		String dir = Paths.get("src", "test", "resources", "sample-base-dir").toString();
		Set<Resource> files = modulesLoader.loadModules(dir, new DefaultModulesFinder(), client);
		assertEquals(26, files.size());
		assertModuleExists("/ext/module1.xqy");
		assertModuleExists("/ext/module1.sjs");
		assertModuleExists("/ext/lib/module2.xqy");
		assertModuleExists("/ext/lib/module2.sjs");
		assertModuleExists("/ext/path.with.dots/inside-dots.xqy");
		assertModuleExists("/ext/rewriter-ext.json");
		assertModuleExists("/ext/rewriter-ext.xml");
		assertModuleExists("/include-module.xqy");
		assertModuleExists("/include-module.sjs");
		assertModuleExists("/module3.xqy");
		assertModuleExists("/module3.sjs");
		assertModuleExists("/rewriter.json");
		assertModuleExists("/rewriter.xml");
		assertModuleExists("/lib/module4.xqy");
		assertModuleExists("/lib/module4.sjs");

		final int initialModuleCount = getUriCountInModulesDatabase();

		// Use a modules manager, but set the timestamp in the future first
		PropertiesModuleManager moduleManager = new PropertiesModuleManager();
		modulesLoader.setAssetFileLoader(new AssetFileLoader(modulesClient, moduleManager));
		modulesLoader.setModulesManager(moduleManager);
		moduleManager.setMinimumFileTimestampToLoad(System.currentTimeMillis() + 10000);
		files = modulesLoader.loadModules(dir, new DefaultModulesFinder(), client);
		assertEquals("No files should have been loaded since the minimum last-modified timestamp is in the future", 0, files.size());

		// run this section twice to test that a bug was fixed in deletePropertiesFile
		for (int i = 0; i < 2; i++) {
			// Remove the timestamp minimum, all the modules should be loaded
			moduleManager.deletePropertiesFile();
			moduleManager.setMinimumFileTimestampToLoad(0);
			files = modulesLoader.loadModules(dir, new DefaultModulesFinder(), client);
			assertEquals("All files should have been loaded since a ModulesManager wasn't used on the first load", 26, files.size());
			assertEquals("No new modules should have been created", initialModuleCount, getUriCountInModulesDatabase());
		}

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
