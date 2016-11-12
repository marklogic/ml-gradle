package com.marklogic.client.modulesloader.impl;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Set;

/**
 * This uses the default Modules database, and it clears it out before the test starts.
 *
 * TODO Move config to a properties file
 */
public class LoadModulesTest extends Assert {

	private DatabaseClient client;
	private DefaultModulesLoader modulesLoader;
	private XccAssetLoader xccAssetLoader;

	private String host = "localhost";
	private String username = "admin";
	private String password = "admin";
	private String database = "Modules";
	private File dir = new File("src/test/resources/static-check");

	@Before
	public void setup() {
		client = DatabaseClientFactory.newClient(host, 8000, database, username, password, DatabaseClientFactory.Authentication.DIGEST);
		client.newServerEval().xquery("cts:uri-match('*.*') ! xdmp:document-delete(.)").eval();

		xccAssetLoader = new XccAssetLoader();
		xccAssetLoader.setUsername(username);
		xccAssetLoader.setHost(host);
		xccAssetLoader.setPassword(password);
		xccAssetLoader.setDatabaseName(database);
		xccAssetLoader.setPort(8000);

		modulesLoader = new DefaultModulesLoader(xccAssetLoader);
		modulesLoader.setModulesManager(null);
	}

	@After
	public void tearDown() {
		client.release();
	}

	@Test
	public void staticCheckAndDontCheckLibraryModules() {
		xccAssetLoader.setStaticCheck(true);
		xccAssetLoader.setBulkLoad(false);
		xccAssetLoader.setStaticCheckLibraryModules(false);

		Set<File> files = modulesLoader.loadModules(dir, new DefaultModulesFinder(), client);
		assertEquals("All 3 modules should have been loaded because we didn't static check the bad libary module",
			3, files.size());
	}

	@Test
	public void staticCheckAndCheckLibraryModules() {
		xccAssetLoader.setStaticCheck(true);
		xccAssetLoader.setBulkLoad(false);

		try {
			modulesLoader.loadModules(dir, new DefaultModulesFinder(), client);
			fail("The load should have failed because of the bad library module");
		} catch (Exception ex) {
			assertTrue(ex.getMessage().contains("/ext/bad-lib.xqy; cause: Unexpected token"));
		}
	}

	@Test
	public void staticCheckAndCheckLibraryModulesAndCatchExceptions() {
		xccAssetLoader.setStaticCheck(true);
		xccAssetLoader.setBulkLoad(false);
		modulesLoader.setCatchExceptions(true);

		Set<File> files = modulesLoader.loadModules(dir, new DefaultModulesFinder(), client);
		assertEquals("The modules should have been loaded, and error messages logged but not thrown", 3, files.size());
	}

	@Test
	public void bulkLoadAndStaticCheck() {
		xccAssetLoader.setBulkLoad(true);
		xccAssetLoader.setStaticCheck(true);
		xccAssetLoader.setStaticCheckLibraryModules(false);

		Set<File> files = modulesLoader.loadModules(dir, new DefaultModulesFinder(), client);
		assertEquals("The load should have succeeded because we didn't check library modules", 3, files.size());
	}

	@Test
	public void bulkLoadAndStaticCheckAndCheckLibraryModules() {
		xccAssetLoader.setBulkLoad(true);
		xccAssetLoader.setStaticCheck(true);
		try {
			modulesLoader.loadModules(dir, new DefaultModulesFinder(), client);
			fail("The load should have failed because of the bad library module");
		} catch (Exception ex) {
			assertTrue(ex.getMessage().contains("Bulk static check failure, cause: Unexpected token"));
		}
	}

	@Test
	public void bulkLoadAndStaticCheckAndCheckLibraryModulesAndCatchExceptions() {
		xccAssetLoader.setBulkLoad(true);
		xccAssetLoader.setStaticCheck(true);
		modulesLoader.setCatchExceptions(true);

		Set<File> files = modulesLoader.loadModules(dir, new DefaultModulesFinder(), client);
		assertEquals("The modules should have been loaded, and error messages logged but not thrown", 3, files.size());
	}
}
