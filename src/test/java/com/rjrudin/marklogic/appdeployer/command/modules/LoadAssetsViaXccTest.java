package com.rjrudin.marklogic.appdeployer.command.modules;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.rjrudin.marklogic.appdeployer.AbstractAppDeployerTest;
import com.rjrudin.marklogic.appdeployer.command.modules.LoadAssetsViaXccCommand;
import com.rjrudin.marklogic.appdeployer.command.restapis.DeployRestApiServersCommand;
import com.rjrudin.marklogic.rest.util.Fragment;
import com.rjrudin.marklogic.xcc.XccTemplate;

public class LoadAssetsViaXccTest extends AbstractAppDeployerTest {

    private XccTemplate xccTemplate;

    @Before
    public void setup() {
        xccTemplate = newModulesXccTemplate();

    }

    @After
    public void tearDown() {
        undeploySampleApp();
    }

    @Test
    public void defaultPermissionsAndCustomCollections() {
        LoadAssetsViaXccCommand command = new LoadAssetsViaXccCommand("src/test/resources/sample-app/more-modules");
        command.setCollections(new String[] { "blue", "red" });

        initializeAppDeployer(new DeployRestApiServersCommand(), command);
        appDeployer.deploy(appConfig);

        assertModulesWereLoaded("/app/hello-lib.xqy", "/app/models/world-lib.xqy");
    }

    @Test
    public void customPermissionsAndMultipleAssetPaths() {
        LoadAssetsViaXccCommand command = new LoadAssetsViaXccCommand("src/test/resources/sample-app/more-modules",
                "src/test/resources/sample-app/other-modules");
        command.setPermissions("rest-admin,read,rest-admin,update,rest-extension-user,execute,rest-extension-user,update");

        initializeAppDeployer(new DeployRestApiServersCommand(), command);
        appDeployer.deploy(appConfig);

        assertModuleHasPermissionCount("/app/hello-lib.xqy", 4);
        assertModuleHasPermissionCount("/app/models/world-lib.xqy", 4);
        assertModuleHasPermissionCount("/other-lib.xqy", 4);
    }

    private void assertModulesWereLoaded(String... uris) {
        for (String uri : uris) {
            assertModuleHasPermissionCount(uri, 3);
            assertModuleIsInTheCustomCollections(uri);
        }
    }

    private void assertModuleHasPermissionCount(String uri, int expectedCount) {
        String response = xccTemplate.executeAdhocQuery(format("xdmp:document-get-permissions('%s')", uri));
        Fragment perms = new Fragment("<xml>" + response + "</xml>");
        assertEquals(expectedCount, perms.getElements("/xml/sec:permission").size());
    }

    private void assertModuleIsInTheCustomCollections(String uri) {
        String response = xccTemplate.executeAdhocQuery(format("xdmp:document-get-collections('%s')", uri));
        String[] collections = response.split("\\n");
        assertEquals(2, collections.length);
        assertEquals("blue", collections[0]);
        assertEquals("red", collections[1]);
    }

}
