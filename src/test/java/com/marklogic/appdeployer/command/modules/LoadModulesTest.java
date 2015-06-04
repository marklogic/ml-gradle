package com.marklogic.appdeployer.command.modules;

import org.junit.Test;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.command.modules.LoadModulesCommand;
import com.marklogic.appdeployer.command.restapis.CreateRestApiServersCommand;

public class LoadModulesTest extends AbstractAppDeployerTest {

    /**
     * TODO Load from multiple paths
     */
    @Test
    public void loadModules() {
        initializeAppDeployer(new CreateRestApiServersCommand(), new LoadModulesCommand());

        appDeployer.deploy(appConfig);
    }
}
