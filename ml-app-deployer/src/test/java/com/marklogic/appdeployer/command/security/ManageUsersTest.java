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
package com.marklogic.appdeployer.command.security;

import org.junit.jupiter.api.BeforeEach;

import com.marklogic.appdeployer.command.AbstractManageResourceTest;
import com.marklogic.appdeployer.command.Command;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.security.UserManager;
import com.marklogic.rest.util.Fragment;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ManageUsersTest extends AbstractManageResourceTest {

    @BeforeEach
    public void setup() {
        appConfig.getCustomTokens().put("CUSTOM_TOKEN_FOR_JOHN_DESCRIPTION", "This was set via a custom token");
    }

    @Override
    protected ResourceManager newResourceManager() {
        return new UserManager(manageClient);
    }

    @Override
    protected Command newCommand() {
        return new DeployUsersCommand();
    }

    @Override
    protected String[] getResourceNames() {
        return new String[] { "sample-app-jane", "sample-app-john" };
    }

    @Override
    protected void afterResourcesCreated() {
        UserManager mgr = new UserManager(manageClient);
        Fragment f = mgr.getAsXml("sample-app-john");
        assertEquals("This was set via a custom token", f.getElementValue("/msec:user-default/msec:description"));
    }
}
