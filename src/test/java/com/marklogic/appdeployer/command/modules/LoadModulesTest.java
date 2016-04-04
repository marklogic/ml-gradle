package com.marklogic.appdeployer.command.modules;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.command.databases.DeployContentDatabasesCommand;
import com.marklogic.appdeployer.command.databases.DeployTriggersDatabaseCommand;
import com.marklogic.appdeployer.command.restapis.DeployRestApiServersCommand;
import com.marklogic.client.modulesloader.impl.AssetFileFilter;
import com.marklogic.junit.PermissionsFragment;
import com.marklogic.xcc.template.XccTemplate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class LoadModulesTest extends AbstractAppDeployerTest {

    private XccTemplate xccTemplate;

    @Before
    public void setup() {
        xccTemplate = new XccTemplate(format("xcc://%s:%s@%s:8000/%s", "admin",
                "admin", appConfig.getHost(), appConfig.getModulesDatabaseName()));
        deleteModuleTimestampsFile();
    }

    @After
    public void teardown() {
        undeploySampleApp();
    }

    @Test
    public void loadModulesFromMultiplePaths() {
        appConfig.getModulePaths().add("src/test/resources/sample-app/build/mlRestApi/some-library/ml-modules");

        initializeAppDeployer(new DeployRestApiServersCommand(true), new LoadModulesCommand());

        appDeployer.deploy(appConfig);

        assertModuleExistsWithDefaultPermissions("sample-lib is loaded from /ext in the default path",
                "/ext/sample-lib.xqy");
        assertModuleExistsWithDefaultPermissions("some-lib.xqy is loaded from the path added at the start of the test",
                "/ext/some-lib.xqy");
    }

    @Test
    public void loadModulesWithCustomPermissions() {
        LoadModulesCommand c = new LoadModulesCommand();
        appConfig.setModulePermissions(appConfig.getModulePermissions() + ",app-user,execute");

        initializeAppDeployer(new DeployRestApiServersCommand(true), c);

        appDeployer.deploy(appConfig);

        PermissionsFragment perms = getDocumentPermissions("/ext/sample-lib.xqy", xccTemplate);
        perms.assertPermissionCount(4);
        perms.assertPermissionExists("rest-admin", "read");
        perms.assertPermissionExists("rest-admin", "update");
        perms.assertPermissionExists("rest-extension-user", "execute");
        perms.assertPermissionExists("app-user", "execute");
    }

    @Test
    public void loadModulesWithAssetFileFilter() {
        LoadModulesCommand c = new LoadModulesCommand();
        appConfig.setAssetFileFilter(new TestFileFilter());

        initializeAppDeployer(new DeployRestApiServersCommand(true), c);
        appDeployer.deploy(appConfig);

        assertEquals("true", xccTemplate.executeAdhocQuery("doc-available('/ext/lib/test.xqy')"));
        assertEquals("false", xccTemplate.executeAdhocQuery("doc-available('/ext/lib/test2.xqy')"));
    }

    @Test
    public void testServerExists() {
        appConfig.getConfigDir().setBaseDir(new File(("src/test/resources/sample-app/db-only-config")));
        appConfig.setTestRestPort(8541);
        initializeAppDeployer(new DeployRestApiServersCommand(true), new LoadModulesCommand());

        appDeployer.deploy(appConfig);

        String[] uris = new String[]{"/Default/sample-app/rest-api/options/sample-app-options.xml", "/Default/sample-app/rest-api/options/sample-app-options.xml"};
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