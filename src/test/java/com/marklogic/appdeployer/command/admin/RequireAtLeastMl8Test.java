package com.marklogic.appdeployer.command.admin;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;

import com.marklogic.appdeployer.AbstractAppDeployerTest;

public class RequireAtLeastMl8Test extends AbstractAppDeployerTest {

    @Test
    public void testThatNoExceptionIsThrown() {
        initializeAppDeployer(new RequireAtLeastMl8Command());
        appDeployer.deploy(appConfig);
    }

    @AfterEach
    public void teardown() {
        undeploySampleApp();
    }
}
