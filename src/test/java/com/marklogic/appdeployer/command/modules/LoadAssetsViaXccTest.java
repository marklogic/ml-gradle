package com.marklogic.appdeployer.command.modules;

import org.junit.After;
import org.junit.Test;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.command.restapis.CreateRestApiServersCommand;

public class LoadAssetsViaXccTest extends AbstractAppDeployerTest {

    @Test
    public void loadModules() {
        initializeAppDeployer(new CreateRestApiServersCommand(), new LoadAssetsViaXccCommand(
                "src/test/resources/sample-app/more-modules"));

        appDeployer.deploy(appConfig);
    }

    @After
    public void tearDown() {
        // undeploySampleApp();
    }
}
