package com.marklogic.client.modulesloader.impl;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.xcc.template.XccTemplate;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Set;

/**
 * This uses the default Modules database, and it clears it out before the test starts.
 * <p>
 * TODO Move config to a properties file
 */
public class LoadModulesTest extends Assert {

	private DatabaseClient client;
	private XccTemplate xccTemplate;
	private DefaultModulesLoader modulesLoader;
	private XccAssetLoader xccAssetLoader;
	private XccStaticChecker staticChecker;

	private String host = "localhost";
	private int port = 8000;
	private String username = "admin";
	private String password = "admin";
	private String database = "Modules";
	private File dir = new File("src/test/resources/static-check");

	@Before
	public void setup() {
		client = DatabaseClientFactory.newClient(host, port, database, username, password, DatabaseClientFactory.Authentication.DIGEST);

		xccTemplate = new XccTemplate("xcc://" + username + ":" + password + "@" + host + ":" + port);
		xccTemplate.executeAdhocQuery("cts:uri-match('*.*') ! xdmp:document-delete(.)");

		staticChecker = new XccStaticChecker(xccTemplate);

		xccAssetLoader = new XccAssetLoader();
		xccAssetLoader.setUsername(username);
		xccAssetLoader.setHost(host);
		xccAssetLoader.setPassword(password);
		xccAssetLoader.setDatabaseName(database);
		xccAssetLoader.setPort(port);

		modulesLoader = new DefaultModulesLoader(xccAssetLoader);
		modulesLoader.setModulesManager(null);
		modulesLoader.setStaticChecker(staticChecker);
	}

	@After
	public void tearDown() {
		client.release();
	}

	@Test
	public void staticCheckAndDontCheckLibraryModules() {
		staticChecker.setBulkCheck(false);
		staticChecker.setCheckLibraryModules(false);

		Set<File> files = modulesLoader.loadModules(dir, new DefaultModulesFinder(), client);
		assertEquals("All 3 modules should have been loaded because we didn't static check the bad libary module",
			3, files.size());
	}

	@Test
	public void staticCheckAndCheckLibraryModules() {
		staticChecker.setBulkCheck(false);
		staticChecker.setCheckLibraryModules(true);

		try {
			modulesLoader.loadModules(dir, new DefaultModulesFinder(), client);
			fail("The load should have failed because of the bad library module");
		} catch (RuntimeException ex) {
			System.out.println(ex.getMessage());
			assertTrue(ex.getMessage().contains("in /ext/bad-lib.xqy, on line 7"));
		}
	}

	@Test
	public void staticCheckAndCheckLibraryModulesAndCatchExceptions() {
		staticChecker.setBulkCheck(false);
		staticChecker.setCheckLibraryModules(true);
		modulesLoader.setCatchExceptions(true);

		Set<File> files = modulesLoader.loadModules(dir, new DefaultModulesFinder(), client);
		assertEquals("The modules should have been loaded, and error messages logged but not thrown", 3, files.size());
	}

	@Test
	public void bulkLoadAndStaticCheck() {
		staticChecker.setBulkCheck(true);
		staticChecker.setCheckLibraryModules(false);

		Set<File> files = modulesLoader.loadModules(dir, new DefaultModulesFinder(), client);
		assertEquals("The load should have succeeded because we didn't check library modules", 3, files.size());
	}

	@Test
	public void bulkLoadAndStaticCheckAndCheckLibraryModules() {
		staticChecker.setBulkCheck(true);
		staticChecker.setCheckLibraryModules(true);

		try {
			modulesLoader.loadModules(dir, new DefaultModulesFinder(), client);
			fail("The load should have failed because of the bad library module");
		} catch (Exception ex) {
			assertTrue(ex.getMessage().contains("in /ext/bad-lib.xqy, on line 7"));
		}
	}

	@Test
	public void bulkLoadAndStaticCheckAndCheckLibraryModulesAndCatchExceptions() {
		staticChecker.setBulkCheck(true);
		staticChecker.setCheckLibraryModules(true);
		modulesLoader.setCatchExceptions(true);

		Set<File> files = modulesLoader.loadModules(dir, new DefaultModulesFinder(), client);
		assertEquals("The modules should have been loaded, and error messages logged but not thrown", 3, files.size());
	}
}
