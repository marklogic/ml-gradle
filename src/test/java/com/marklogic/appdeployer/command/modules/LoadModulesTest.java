package com.marklogic.appdeployer.command.modules;

import org.junit.Test;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.command.restapis.CreateRestApiServersCommand;
import com.marklogic.xccutil.template.XccTemplate;

public class LoadModulesTest extends AbstractAppDeployerTest {

    private XccTemplate xccTemplate;

    @Test
    public void loadModulesFromMultiplePaths() {
        appConfig.getModulePaths().add("src/test/resources/sample-app/build/mlRestApi/some-library/ml-modules");

        initializeAppDeployer(new CreateRestApiServersCommand(), new LoadModulesCommand());

        appDeployer.deploy(appConfig);

        assertModuleExists("sample-lib is loaded from /ext in the default path", "/ext/sample-lib.xqy");
        assertModuleExists("some-lib.xqy is loaded from the path added at the start of the test", "/ext/some-lib.xqy");
    }

    private void assertModuleExists(String message, String path) {
        if (xccTemplate == null) {
            xccTemplate = newModulesXccTemplate();
        }

        assertEquals(message, "true", xccTemplate.executeAdhocQuery(format("fn:doc-available('%s')", path)));
    }
}
