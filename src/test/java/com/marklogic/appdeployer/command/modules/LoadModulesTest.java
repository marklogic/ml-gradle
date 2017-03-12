package com.marklogic.appdeployer.command.modules;

import java.io.File;

import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.client.modulesloader.impl.DefaultModulesLoader;
import com.marklogic.junit.Fragment;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.command.restapis.DeployRestApiServersCommand;
import com.marklogic.client.modulesloader.impl.AssetFileFilter;
import com.marklogic.junit.PermissionsFragment;
import com.marklogic.xcc.template.XccTemplate;

public class LoadModulesTest extends AbstractAppDeployerTest {

    private XccTemplate xccTemplate;

    @Before
    public void setup() {
        xccTemplate = new XccTemplate(format("xcc://%s:%s@%s:8000/%s", "admin", "admin", appConfig.getHost(),
                appConfig.getModulesDatabaseName()));
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
		initializeAppDeployer(new DeployRestApiServersCommand(true), new LoadModulesCommand());
		appDeployer.deploy(appConfig);
		assertTrue("The custom file should have been created when the modules were loaded", customFile.exists());
	}

    @Test
    public void loadModulesFromMultiplePaths() {
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

        PermissionsFragment perms = getDocumentPermissions("/ext/sample-lib.xqy", xccTemplate);
        perms.prettyPrint();
        perms.assertPermissionCount(4);
        perms.assertPermissionExists("rest-admin", "read");
        perms.assertPermissionExists("rest-admin", "update");
        perms.assertPermissionExists("rest-extension-user", "execute");
        perms.assertPermissionExists("app-user", "execute");
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
        appConfig.getCustomTokens().put("COLOR", "red");
        appConfig.getCustomTokens().put("DESCRIPTION", "${COLOR} description");

        initializeAppDeployer(new DeployRestApiServersCommand(true), buildLoadModulesCommand());
        appDeployer.deploy(appConfig);

        assertEquals("true", xccTemplate.executeAdhocQuery("doc-available('/ext/lib/test.xqy')"));
        assertEquals("false", xccTemplate.executeAdhocQuery("doc-available('/ext/lib/test2.xqy')"));

        String xml = xccTemplate.executeAdhocQuery("doc('/ext/lib/test.xqy')");
        Fragment f = parse(xml);
        f.assertElementValue("/test/color", "red");
        f.assertElementValue("/test/description", "red description");

    }

    @Test
    public void testServerExists() {
        appConfig.getConfigDir().setBaseDir(new File(("src/test/resources/sample-app/db-only-config")));
        appConfig.setTestRestPort(8541);
        initializeAppDeployer(new DeployRestApiServersCommand(true), buildLoadModulesCommand());

        appDeployer.deploy(appConfig);

        String[] uris = new String[] { "/Default/sample-app/rest-api/options/sample-app-options.xml",
                "/Default/sample-app/rest-api/options/sample-app-options.xml" };
        for (String uri : uris) {
            assertEquals("true", xccTemplate.executeAdhocQuery(format("doc-available('%s')", uri)));
        }
    }

    private void assertModuleExistsWithDefaultPermissions(String message, String uri) {
        assertEquals(message, "true", xccTemplate.executeAdhocQuery(format("fn:doc-available('%s')", uri)));
        assertDefaultPermissionsExists(uri);
    }

    /**
     * Apparently, the REST API won't let you remove these 3 default permissions, they're always present.
     */
    private void assertDefaultPermissionsExists(String uri) {
        PermissionsFragment perms = getDocumentPermissions(uri, xccTemplate);
        perms.assertPermissionCount(3);
        perms.assertPermissionExists("rest-admin", "read");
        perms.assertPermissionExists("rest-admin", "update");
        perms.assertPermissionExists("rest-extension-user", "execute");
    }
}

class TestFileFilter extends AssetFileFilter {

    @Override
    public boolean accept(File f) {
        return !f.getName().equals("test2.xqy") && super.accept(f);
    }

}
