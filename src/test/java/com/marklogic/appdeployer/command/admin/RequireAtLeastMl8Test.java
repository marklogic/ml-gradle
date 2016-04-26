package com.marklogic.appdeployer.command.admin;

import org.junit.Test;
import org.junit.After;

import com.marklogic.appdeployer.AbstractAppDeployerTest;

public class RequireAtLeastMl8Test extends AbstractAppDeployerTest {

    @Test
    public void testThatNoExceptionIsThrown() {
        initializeAppDeployer(new RequireAtLeastMl8Command());
        appDeployer.deploy(appConfig);
    }

    @After
    public void teardown() {
        undeploySampleApp();
    }
}
