package com.marklogic.appdeployer.command.modules;

import org.junit.Test;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.command.modules.LoadModulesPlugin;
import com.marklogic.appdeployer.command.restapis.CreateRestApiServersPlugin;

public class LoadModulesTest extends AbstractAppDeployerTest {

    /**
     * TODO Load from multiple paths
     */
    @Test
    public void loadModules() {
        initializeAppDeployer(new CreateRestApiServersPlugin(), new LoadModulesPlugin());

        appDeployer.deploy(appConfig);
    }
}
