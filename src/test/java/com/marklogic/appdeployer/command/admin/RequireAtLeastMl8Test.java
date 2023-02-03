/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
