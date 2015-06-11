package com.marklogic.appdeployer.command.modules;

import org.junit.Test;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.command.restapis.CreateRestApiServersCommand;
import com.marklogic.rest.mgmt.PermissionsFragment;
import com.marklogic.xccutil.template.XccTemplate;

public class LoadModulesTest extends AbstractAppDeployerTest {

    private XccTemplate xccTemplate;

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

    }

    private void assertModuleExistsWithDefaultPermissions(String message, String uri) {
        if (xccTemplate == null) {
            xccTemplate = newModulesXccTemplate();
        }

        assertEquals(message, "true", xccTemplate.executeAdhocQuery(format("fn:doc-available('%s')", uri)));

        PermissionsFragment perms = getDocumentPermissions(uri, xccTemplate);
        perms.assertPermissionExists("rest-admin", "read");
        perms.assertPermissionExists("rest-admin", "update");
        perms.assertPermissionExists("rest-extension-user", "execute");
    }
}
