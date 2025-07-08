/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
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
