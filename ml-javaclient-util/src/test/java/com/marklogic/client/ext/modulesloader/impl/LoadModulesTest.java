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
package com.marklogic.client.ext.modulesloader.impl;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.admin.ServerConfigurationManager;
import com.marklogic.client.ext.AbstractIntegrationTest;
import com.marklogic.client.ext.modulesloader.Modules;
import com.marklogic.client.ext.modulesloader.ModulesFinder;
import com.marklogic.client.ext.tokenreplacer.DefaultTokenReplacer;
import com.marklogic.client.io.BytesHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.StringHandle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class LoadModulesTest extends AbstractIntegrationTest {

	private DatabaseClient modulesClient;
	private DefaultModulesLoader modulesLoader;

	@BeforeEach
	public void setup() {
		client = newClient(MODULES_DATABASE);
		client.newServerEval().xquery("cts:uris((), (), cts:true-query()) ! xdmp:document-delete(.)").eval();
		modulesClient = client;
		assertEquals(0, getUriCountInModulesDatabase(), "No modules should exist");

		/**
		 * Odd - the Client REST API doesn't allow for loading namespaces when the DatabaseClient has a database
		 * specified, so we construct a DatabaseClient without a database and assume we get the expected content
		 * database.
		 */
		String currentDatabase = clientConfig.getDatabase();
		clientConfig.setDatabase(null);
		client = configuredDatabaseClientFactory.newDatabaseClient(clientConfig);
		clientConfig.setDatabase(currentDatabase);

		modulesLoader = new DefaultModulesLoader(new AssetFileLoader(modulesClient));
		modulesLoader.setModulesManager(null);

		File file = new File(PropertiesModuleManager.DEFAULT_FILE_PATH);
		if (file.exists()) {
			file.delete();
		}
	}

	@Test
	public void jsonRestPropertiesFile() {
		ServerConfigurationManager mgr = client.newServerConfigManager();
		try {
			String dir = Paths.get("src", "test", "resources", "json-rest-properties").toString();
			ModulesFinder finder = new DefaultModulesFinder();
			Modules modules = finder.findModules(dir);
			assertTrue(modules.getPropertiesFile().exists());

			// Now load the modules for real
			modulesLoader.loadModules(dir, finder, client);

			mgr.readConfiguration();
			assertTrue(mgr.getQueryValidation());
			assertTrue(mgr.getQueryOptionValidation());
			assertEquals(ServerConfigurationManager.UpdatePolicy.OVERWRITE_METADATA, mgr.getUpdatePolicy());
			assertTrue(mgr.getServerRequestLogging());
			assertTrue(StringUtils.isEmpty(mgr.getDefaultDocumentReadTransform()));
			assertTrue(mgr.getDefaultDocumentReadTransformAll());
		} finally {
			setRestPropertiesToMarkLogicDefaults();
		}
	}

	@Test
	public void xmlRestPropertiesFile() {
		ServerConfigurationManager mgr = client.newServerConfigManager();
		try {
			String dir = Paths.get("src", "test", "resources", "xml-rest-properties").toString();
			ModulesFinder finder = new DefaultModulesFinder();
			Modules modules = finder.findModules(dir);
			assertTrue(modules.getPropertiesFile().exists());

			// Now load the modules for real
			modulesLoader.loadModules(dir, finder, client);

			mgr.readConfiguration();
			assertTrue(mgr.getQueryValidation());
			assertTrue(mgr.getQueryOptionValidation());
			assertEquals(ServerConfigurationManager.UpdatePolicy.OVERWRITE_METADATA, mgr.getUpdatePolicy());
			assertTrue(mgr.getServerRequestLogging());
			assertTrue(StringUtils.isEmpty(mgr.getDefaultDocumentReadTransform()));
			assertTrue(mgr.getDefaultDocumentReadTransformAll());
		} finally {
			setRestPropertiesToMarkLogicDefaults();
		}
	}

	private void setRestPropertiesToMarkLogicDefaults() {
		ServerConfigurationManager mgr = client.newServerConfigManager();
		mgr.setQueryValidation(false);
		mgr.setQueryOptionValidation(true);
		mgr.setUpdatePolicy(ServerConfigurationManager.UpdatePolicy.MERGE_METADATA);
		mgr.setDefaultDocumentReadTransform(null);
		mgr.setDefaultDocumentReadTransformAll(true);
		mgr.setServerRequestLogging(false);
		mgr.writeConfiguration();
	}

	@Test
	public void pathWithSpaces() {
		String dir = Paths.get("src", "test", "resources", "path with spaces").toString();
		ModulesFinder finder = new DefaultModulesFinder();
		Modules modules = finder.findModules(dir);
		List<Resource> assetDirectories = modules.getAssetDirectories();
		assertEquals(1, assetDirectories.size(), "Expecting one directory, the 'root' directory");

		// Now load the modules for real
		modulesLoader.loadModules(dir, finder, client);
		String moduleXml = modulesClient.newXMLDocumentManager().read("/example/example.xqy", new StringHandle()).get();
		assertEquals("<example/>", moduleXml.trim());
	}

	@Test
	public void invalidRestModule() {
		String dir = Paths.get("src", "test", "resources", "invalid-rest-modules").toString();

		try {
			modulesLoader.loadModules(dir, new DefaultModulesFinder(), client);
			fail("Loading modules should have failed because of an invalid REST options file");
		} catch (RuntimeException re) {
			assertTrue(re.getMessage().contains("RESTAPI-INVALIDCONTENT"),
				"Unexpected message: " + re.getMessage());
		}

		// This should now succeed since DefaultModulesLoader won't rethrow the REST module failure
		modulesLoader.setRethrowRestModulesFailure(false);
		modulesLoader.loadModules(dir, new DefaultModulesFinder(), client);
	}

	@Test
	public void catchExceptionsForInvalidModules() {
		String dir = Paths.get("src", "test", "resources", "bad-modules").toString();

		try {
			modulesLoader.loadModules(dir, new DefaultModulesFinder(), client);
			fail("Loading modules should have failed because of an invalid JSON file");
		} catch (RuntimeException re) {
			assertTrue(re.getMessage().contains("Unexpected end of file in JSON"));
		}

		modulesLoader.setCatchExceptions(true);
		// This should now succeed since we're catching exceptions
		Set<Resource> loadedModules = modulesLoader.loadModules(dir, new DefaultModulesFinder(), client);
		assertTrue(loadedModules.isEmpty(), "There's only the one invalid module, so nothing should have been loaded");
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
			"/Default/ml-javaclient-util-test/rest-api/options/sample-options.xml", new StringHandle()).get();
		assertTrue(optionsXml.contains("fn:collection('hello-world')"));

		String serviceText = new String(modulesClient.newDocumentManager().read(
			"/marklogic.rest.resource/sample/assets/resource.xqy", new BytesHandle()).get());
		assertTrue(serviceText.contains("xdmp:log(\"hello-world called\")"));

		String transformText = new String(modulesClient.newDocumentManager().read(
			"/marklogic.rest.transform/xquery-transform/assets/transform.xqy", new BytesHandle()).get());
		assertTrue(transformText.contains("xdmp:log(\"hello-world\")"));
	}

	@Test
	public void customBatchSize() {
		initializeModulesLoaderWithAssetBatchSize(2);
		verifyModuleCountWithPattern(".*/ext/.*", "Should load every file", 7);
	}

	/**
	 * Just ignoring an invalid batch size, which is anything less than 1.
	 */
	@Test
	public void invalidBatchSize() {
		initializeModulesLoaderWithAssetBatchSize(-1);
		verifyModuleCountWithPattern(".*/ext/.*", "Should load every file", 7);
	}

	@Test
	public void withFilenamePattern() {
		verifyModuleCountWithPattern(".*options.*(xml)", "Should only load the single XML options file", 1);
		verifyModuleCountWithPattern(".*transforms.*", "Should only load the 5 transforms", 5);
		verifyModuleCountWithPattern(".*services.*", "Should only load the 3 services", 3);
		verifyModuleCountWithPattern(".*", "Should load every file", 26);
		verifyModuleCountWithPattern(".*/ext.*(lib|dots)/.*xqy", "Should only load the xqy asset modules " +
			"under ext/lib and ext/path/with/dots", 2);
	}

	private void verifyModuleCountWithPattern(String pattern, String message, int count) {
		String dir = Paths.get("src", "test", "resources", "sample-base-dir").toString();
		modulesLoader.setIncludeFilenamePattern(Pattern.compile(pattern));
		Set<Resource> files = modulesLoader.loadModules(dir, new DefaultModulesFinder(), client);
		assertEquals(count, files.size(), message);
	}

	@Test
	public void loadModulesAndAccountForHost() {
		PropertiesModuleManager moduleManager = new PropertiesModuleManager(PropertiesModuleManager.DEFAULT_FILE_PATH, modulesClient);
		modulesLoader.setAssetFileLoader(new AssetFileLoader(modulesClient, moduleManager));
		modulesLoader.setModulesManager(moduleManager);

		String dir = Paths.get("src", "test", "resources", "sample-base-dir").toString();
		Set<Resource> files = modulesLoader.loadModules(dir, new DefaultModulesFinder(), client);
		assertEquals(26, files.size());

		files = modulesLoader.loadModules(dir, new DefaultModulesFinder(), client);
		assertEquals(0, files.size(), "No files should have been loaded since none were new or modified");

		// The host defaults to "localhost", so change it to a host that should still hit localhost on any OS, and
		// verify all files were loaded because a different host was used
		moduleManager.setHost("127.0.0.1");
		files = modulesLoader.loadModules(dir, new DefaultModulesFinder(), client);
		assertEquals(26, files.size());

		files = modulesLoader.loadModules(dir, new DefaultModulesFinder(), client);
		assertEquals(0, files.size(), "No files should have been loaded since none were new or modified");
	}

	@Test
	public void withPropertiesFiles() {
		AssetFileLoader fileLoader = new AssetFileLoader(modulesClient);
		fileLoader.setPermissions("rest-extension-user,read,rest-extension-user,update,rest-extension-user,execute");
		modulesLoader.setAssetFileLoader(fileLoader);

		String dir = Paths.get("src", "test", "resources", "base-dir-with-properties-files").toString();
		Set<Resource> files = modulesLoader.loadModules(dir, new DefaultModulesFinder(), client);
		assertEquals(2, files.size());

		DocumentMetadataHandle metadata = new DocumentMetadataHandle();

		modulesClient.newDocumentManager().readMetadata("/root.sjs", metadata);
		assertEquals(1, metadata.getCollections().size());
		assertEquals("parent", metadata.getCollections().iterator().next());
		DocumentMetadataHandle.DocumentPermissions perms = metadata.getPermissions();
		assertEquals(DocumentMetadataHandle.Capability.READ, perms.get("qconsole-user").iterator().next());
		assertEquals(3, perms.get("rest-extension-user").size());

		modulesClient.newDocumentManager().readMetadata("/lib/lib.sjs", metadata);
		assertEquals(1, metadata.getCollections().size());
		assertEquals("lib", metadata.getCollections().iterator().next());
		perms = metadata.getPermissions();
		assertEquals(DocumentMetadataHandle.Capability.UPDATE, perms.get("app-user").iterator().next());
		assertEquals(3, perms.get("rest-extension-user").size());
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
		assertEquals(0, files.size(), "No files should have been loaded since the minimum last-modified timestamp is in the future");

		// run this section twice to test that a bug was fixed in deletePropertiesFile
		for (int i = 0; i < 2; i++) {
			// Remove the timestamp minimum, all the modules should be loaded
			moduleManager.deletePropertiesFile();
			moduleManager.setMinimumFileTimestampToLoad(0);
			files = modulesLoader.loadModules(dir, new DefaultModulesFinder(), client);
			assertEquals(26, files.size(), "All files should have been loaded since a ModulesManager wasn't used on the first load");
			assertEquals(initialModuleCount, getUriCountInModulesDatabase(), "No new modules should have been created");
		}

		// Load again; this time, no files should have been loaded
		files = modulesLoader.loadModules(dir, new DefaultModulesFinder(), client);
		assertEquals(0, files.size(), "No files should have been loaded since none were new or modified");
		assertEquals(initialModuleCount, getUriCountInModulesDatabase(), "Module count shouldn't have changed either");

	}

	private int getUriCountInModulesDatabase() {
		return Integer.parseInt(modulesClient.newServerEval().xquery("count(cts:uris((), (), cts:true-query()))").evalAs(String.class));
	}

	private void assertModuleExists(String uri) {
		assertEquals("true",
			modulesClient.newServerEval().xquery(String.format("fn:doc-available('%s')", uri)).evalAs(String.class)
		);
	}

	private void initializeModulesLoaderWithAssetBatchSize(int batchSize) {
		AssetFileLoader assetFileLoader = new AssetFileLoader(modulesClient);
		assetFileLoader.setBatchSize(batchSize);
		modulesLoader = new DefaultModulesLoader(assetFileLoader);
		modulesLoader.setModulesManager(null);
	}

}
