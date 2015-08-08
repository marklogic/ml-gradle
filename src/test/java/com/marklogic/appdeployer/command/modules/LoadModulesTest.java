package com.marklogic.appdeployer.command.modules;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.command.restapis.CreateRestApiServersCommand;
import com.rjrudin.marklogic.junit.PermissionsFragment;
import com.rjrudin.marklogic.xcc.XccTemplate;

public class LoadModulesTest extends AbstractAppDeployerTest {

    private XccTemplate xccTemplate;

    @Before
    public void setup() {
        xccTemplate = newModulesXccTemplate();
        deleteModuleTimestampsFile();
    }

    @After
    public void teardown() {
        undeploySampleApp();
    }

    @Test
    public void loadModulesFromMultiplePaths() {
        appConfig.getModulePaths().add("src/test/resources/sample-app/build/mlRestApi/some-library/ml-modules");

        initializeAppDeployer(new CreateRestApiServersCommand(), new LoadModulesCommand());

        appDeployer.deploy(appConfig);

        assertModuleExistsWithDefaultPermissions("sample-lib is loaded from /ext in the default path",
                "/ext/sample-lib.xqy");
        assertModuleExistsWithDefaultPermissions("some-lib.xqy is loaded from the path added at the start of the test",
                "/ext/some-lib.xqy");
    }

    @Test
    public void loadModulesWithCustomPermissions() {
        LoadModulesCommand c = new LoadModulesCommand();
        c.setCustomAssetRolesAndCapabilities("app-user,execute");

        initializeAppDeployer(new CreateRestApiServersCommand(), c);

        appDeployer.deploy(appConfig);

        PermissionsFragment perms = getDocumentPermissions("/ext/sample-lib.xqy", xccTemplate);
        perms.assertPermissionCount(4);
        perms.assertPermissionExists("rest-admin", "read");
        perms.assertPermissionExists("rest-admin", "update");
        perms.assertPermissionExists("rest-extension-user", "execute");
        perms.assertPermissionExists("app-user", "execute");
    }

    @Test
    public void testServerExists() {
        appConfig.setTestRestPort(8541);
        initializeAppDeployer(new CreateRestApiServersCommand(), new LoadModulesCommand());
        appDeployer.deploy(appConfig);

        assertEquals(
                "true",
                xccTemplate
                        .executeAdhocQuery("fn:doc-available('/Default/sample-app/rest-api/options/sample-app-options.xml')"));
        assertEquals(
                "true",
                xccTemplate
                        .executeAdhocQuery("fn:doc-available('/Default/sample-app-test/rest-api/options/sample-app-options.xml')"));
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
