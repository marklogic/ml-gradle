package com.marklogic.appdeployer.command.modules;

import org.junit.After;
import org.junit.Test;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.command.restapis.CreateRestApiServersCommand;
import com.marklogic.rest.util.Fragment;
import com.marklogic.xccutil.template.XccTemplate;

public class LoadAssetsViaXccTest extends AbstractAppDeployerTest {

    private XccTemplate xccTemplate;

    @Test
    public void defaultPermissionsAndCustomCollections() {
        LoadAssetsViaXccCommand command = new LoadAssetsViaXccCommand("src/test/resources/sample-app/more-modules");
        command.setCollections(new String[] { "blue", "red" });

        initializeAppDeployer(new CreateRestApiServersCommand(), command);

        appDeployer.deploy(appConfig);

        xccTemplate = newModulesXccTemplate();

        assertModulesWereLoaded("/app/hello-lib.xqy", "/app/models/world-lib.xqy");
    }

    private void assertModulesWereLoaded(String... uris) {
        for (String uri : uris) {
            assertModuleHasTheDefaultPermissions(uri);
            assertModuleIsInTheCustomCollections(uri);
        }
    }

    private void assertModuleHasTheDefaultPermissions(String uri) {
        String response = xccTemplate.executeAdhocQuery(format("xdmp:document-get-permissions('%s')", uri));
        Fragment perms = new Fragment("<xml>" + response + "</xml>");
        assertEquals(format("By default, the module %s should have 3 permissions", uri), 3,
                perms.getElements("/xml/sec:permission").size());
    }

    private void assertModuleIsInTheCustomCollections(String uri) {
        String response = xccTemplate.executeAdhocQuery(format("xdmp:document-get-collections('%s')", uri));
        String[] collections = response.split("\\n");
        assertEquals(2, collections.length);
        assertEquals("blue", collections[0]);
        assertEquals("red", collections[1]);
    }

    @After
    public void tearDown() {
        // undeploySampleApp();
    }
}
