package com.marklogic.client.modulesloader.impl;

import com.marklogic.client.AbstractIntegrationTest;
import com.marklogic.xcc.template.XccTemplate;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Set;

/**
 * This uses the default Modules database, and it clears it out before the test starts.
 */
public class LoadModulesTest extends AbstractIntegrationTest {

	private XccTemplate xccTemplate;
	private DefaultModulesLoader modulesLoader;
	private XccAssetLoader xccAssetLoader;
	private XccStaticChecker staticChecker;

	private String database = "Modules";
	private File dir = new File("src/test/resources/static-check");

	@Before
	public void setup() {
		client = newClient(database);

		xccTemplate = new XccTemplate("xcc://" + clientConfig.getUsername() + ":" + clientConfig.getPassword()
			+ "@" + clientConfig.getHost() + ":" + clientConfig.getPort());

		xccTemplate.executeAdhocQuery("cts:uri-match('*.*') ! xdmp:document-delete(.)");

		staticChecker = new XccStaticChecker(xccTemplate);

		xccAssetLoader = new XccAssetLoader();
		xccAssetLoader.setUsername(clientConfig.getUsername());
		xccAssetLoader.setHost(clientConfig.getHost());
		xccAssetLoader.setPassword(clientConfig.getPassword());
		xccAssetLoader.setDatabaseName(database);
		xccAssetLoader.setPort(clientConfig.getPort());

		modulesLoader = new DefaultModulesLoader(xccAssetLoader);
		modulesLoader.setModulesManager(null);
		modulesLoader.setStaticChecker(staticChecker);
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
