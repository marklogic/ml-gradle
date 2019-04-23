package com.marklogic.appdeployer.command.modules;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.command.restapis.DeployRestApiServersCommand;
import com.marklogic.client.ext.modulesloader.impl.DefaultFileFilter;
import com.marklogic.client.ext.modulesloader.impl.DefaultModulesLoader;
import com.marklogic.client.ext.modulesloader.impl.PropertiesModuleManager;
import com.marklogic.junit.Fragment;
import com.marklogic.junit.PermissionsFragment;
import com.marklogic.xcc.template.XccTemplate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.regex.Pattern;

public class LoadModulesTest extends AbstractAppDeployerTest {

	private XccTemplate modulesXccTemplate;

	@Before
	public void setup() {
		modulesXccTemplate = new XccTemplate(appConfig.getHost(), appConfig.getAppServicesPort(), appConfig.getRestAdminUsername(),
			appConfig.getRestAdminPassword(), appConfig.getModulesDatabaseName());
	}

	@After
	public void teardown() {
		undeploySampleApp();
	}

	@Test
	public void loadModulesWithStaticCheck() {
		appConfig.getModulePaths().clear();
		appConfig.getModulePaths().add("src/test/resources/sample-app/static-check-modules");
		appConfig.setStaticCheckAssets(false);
		initializeAppDeployer(new DeployRestApiServersCommand(true), buildLoadModulesCommand());

		// This should succeed because modules aren't statically checked
		appDeployer.deploy(appConfig);

		// Now load modules with static check enabled
		appConfig.setStaticCheckAssets(true);
		initializeAppDeployer(buildLoadModulesCommand());
		try {
			appDeployer.deploy(appConfig);
			fail("An error should have been thrown because /ext/bad.xqy failed static check");
		} catch (Exception ex) {
			String message = ex.getMessage();
			assertTrue("Loading modules with 2.11.0 of ml-javaclient-util defaults to bulk loading, so static checking should as well; message: " + message,
				message.contains("Unexpected token syntax error"));
			assertTrue(message.contains("in /ext/bad.xqy, on line 2"));
		} finally {
			initializeAppDeployer(new DeployRestApiServersCommand(true));
		}
	}

	@Test
	public void customModuleTimestampsPath() {
		String path = "build/custom-path.properties";
		File customFile = new File(path);
		customFile.mkdirs();
		if (customFile.exists()) {
			customFile.delete();
		}

		appConfig.setModuleTimestampsPath("build/custom-path.properties");
		LoadModulesCommand command = new LoadModulesCommand();
		initializeAppDeployer(new DeployRestApiServersCommand(true), command);
		appDeployer.deploy(appConfig);
		assertTrue("The custom file should have been created when the modules were loaded", customFile.exists());

		DefaultModulesLoader loader = (DefaultModulesLoader)command.getModulesLoader();
		PropertiesModuleManager manager = (PropertiesModuleManager)loader.getModulesManager();
		assertEquals("The host should have been set on the PropertiesModuleManager via DefaultModulesLoaderFactory " +
			"so that module timestamps account for the host", appConfig.getHost(), manager.getHost());
	}

	@Test
	public void loadModulesFromMultiplePaths() {
		// Setting batch size just to verify that nothing blows up when doing so
		appConfig.setModulesLoaderBatchSize(1);
		appConfig.getModulePaths().add("src/test/resources/sample-app/build/mlRestApi/some-library/ml-modules");

		initializeAppDeployer(new DeployRestApiServersCommand(true), buildLoadModulesCommand());
		appDeployer.deploy(appConfig);

		assertModuleExistsWithDefaultPermissions("sample-lib is loaded from /ext in the default path",
			"/ext/sample-lib.xqy");
		assertModuleExistsWithDefaultPermissions("some-lib.xqy is loaded from the path added at the start of the test",
			"/ext/some-lib.xqy");
	}

	@Test
	public void loadModulesWithCustomPermissions() {
		appConfig.setModulePermissions(appConfig.getModulePermissions() + ",app-user,execute");

		initializeAppDeployer(new DeployRestApiServersCommand(true), buildLoadModulesCommand());

		appDeployer.deploy(appConfig);

		PermissionsFragment perms = getDocumentPermissions("/ext/sample-lib.xqy", modulesXccTemplate);
		perms.assertPermissionCount(6);

		// Default permissions set by AppConfig
		perms.assertPermissionExists("rest-admin", "read");
		perms.assertPermissionExists("rest-admin", "update");
		perms.assertPermissionExists("rest-extension-user", "execute");

		// Custom permission
		perms.assertPermissionExists("app-user", "execute");

		// Permissions that the REST API still applies, which seems like a bug
		perms.assertPermissionExists("rest-reader", "read");
		perms.assertPermissionExists("rest-writer", "update");
	}

	@Test
	public void loadModulesWithAssetFileFilterAndTokenReplacement() {
		appConfig.setAssetFileFilter(new TestFileFilter());

		/**
		 * Add a couple tokens to replace in the modules. It's still a good practice to ensure these tokens don't
		 * hit on anything accidentally, so their names are capitalized. But since the module token replacement
		 * follows the Roxy convention by default and prefixes properties with "@ml.", our modules then need
		 * "@ml.%%COLOR%%", for example.
		 */
		appConfig.setUseRoxyTokenPrefix(true);
		appConfig.getCustomTokens().put("COLOR", "red");
		appConfig.getCustomTokens().put("DESCRIPTION", "${COLOR} description");

		initializeAppDeployer(new DeployRestApiServersCommand(true), buildLoadModulesCommand());
		appDeployer.deploy(appConfig);

		assertEquals("true", modulesXccTemplate.executeAdhocQuery("doc-available('/ext/lib/test.xqy')"));
		assertEquals("false", modulesXccTemplate.executeAdhocQuery("doc-available('/ext/lib/test2.xqy')"));

		String xml = modulesXccTemplate.executeAdhocQuery("doc('/ext/lib/test.xqy')");
		Fragment f = parse(xml);
		f.assertElementValue("/test/color", "red");
		f.assertElementValue("/test/description", "red description");
	}

	@Test
	public void testServerExists() {
		appConfig.getFirstConfigDir().setBaseDir(new File(("src/test/resources/sample-app/db-only-config")));
		appConfig.setTestRestPort(8541);
		initializeAppDeployer(new DeployRestApiServersCommand(true), buildLoadModulesCommand());

		appDeployer.deploy(appConfig);

		String[] uris = new String[]{"/Default/sample-app/rest-api/options/sample-app-options.xml",
			"/Default/sample-app/rest-api/options/sample-app-options.xml"};
		for (String uri : uris) {
			assertEquals("true", modulesXccTemplate.executeAdhocQuery(format("doc-available('%s')", uri)));
		}
	}

	@Test
	public void deleteTestModules() {
		appConfig.setDeleteTestModules(true);
		appConfig.setDeleteTestModulesPattern("/ext/lib/*.xqy");

		initializeAppDeployer(new DeployRestApiServersCommand(true), buildLoadModulesCommand(),
			new DeleteTestModulesCommand());
		appDeployer.deploy(appConfig);

		String xquery = "fn:count(cts:uri-match('/ext/**.xqy'))";
		assertEquals(1, Integer.parseInt(modulesXccTemplate.executeAdhocQuery(xquery)));
	}

	@Test
	public void includeModulesPattern() {
		appConfig.setModuleFilenamesIncludePattern(Pattern.compile(".*/ext.*"));

		initializeAppDeployer(new DeployRestApiServersCommand(true), buildLoadModulesCommand());
		appDeployer.deploy(appConfig);

		String xquery = "fn:count(cts:uris((), (), cts:true-query()))";
		assertEquals("Should have the 3 /ext modules plus the REST API properties file, which was loaded when the REST server was created",
			4, Integer.parseInt(modulesXccTemplate.executeAdhocQuery(xquery)));
	}

	private void assertModuleExistsWithDefaultPermissions(String message, String uri) {
		assertEquals(message, "true", modulesXccTemplate.executeAdhocQuery(format("fn:doc-available('%s')", uri)));
		assertDefaultPermissionsExists(uri);
	}

	/**
	 * Apparently, the REST API won't let you remove these 3 default permissions, they're always present.
	 * <p>
	 * And, now that we're loading modules via the REST API by default, rest-reader/read and rest-writer/update are
	 * always present, at least on 8.0-6.3 and 9.0-1.1, which seems like a bug.
	 */
	private void assertDefaultPermissionsExists(String uri) {
		PermissionsFragment perms = getDocumentPermissions(uri, modulesXccTemplate);
		perms.assertPermissionCount(5);
		perms.assertPermissionExists("rest-admin", "read");
		perms.assertPermissionExists("rest-admin", "update");
		perms.assertPermissionExists("rest-extension-user", "execute");

		// Not really expected!
		perms.assertPermissionExists("rest-reader", "read");
		perms.assertPermissionExists("rest-writer", "update");
	}

}

class TestFileFilter extends DefaultFileFilter {

	@Override
	public boolean accept(File f) {
		return !f.getName().equals("test2.xqy") && super.accept(f);
	}

}
