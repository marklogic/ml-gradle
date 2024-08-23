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

import java.io.File;

import org.junit.jupiter.api.Test;

import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.AbstractManageResourceTest;
import com.marklogic.appdeployer.command.Command;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.security.PrivilegeManager;
import com.marklogic.mgmt.resource.security.RoleManager;
import com.marklogic.rest.util.Fragment;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ManageRolesTest extends AbstractManageResourceTest {

    @Override
    protected ResourceManager newResourceManager() {
        return new RoleManager(manageClient);
    }

    @Override
    protected Command newCommand() {
        return new DeployRolesCommand();
    }

    @Override
    protected String[] getResourceNames() {
        return new String[] { "sample-app-role1", "sample-app-role2" };
    }

    @Test
    public void updateRole() {
        RoleManager mgr = new RoleManager(manageClient);
        initializeAppDeployer(new DeployRolesCommand());

        appDeployer.deploy(appConfig);

        assertTrue(mgr.exists("sample-app-role1"));

        try {
            mgr.save("{\"role-name\": \"sample-app-role1\", \"description\":\"This is an updated description\"}");

            Fragment f = mgr.getAsXml("sample-app-role1");
            assertTrue(f.elementExists("/msec:role-default/msec:description[. = 'This is an updated description']"),
				"The save call should either create or update a role");
        } finally {
            appDeployer.undeploy(appConfig);
        }

    }

    @Test
    public void roleWithCustomPrivilege() {
        appConfig.setConfigDir(new ConfigDir(new File("src/test/resources/sample-app/role-with-privilege-config")));

        RoleManager roleMgr = new RoleManager(manageClient);
        PrivilegeManager privMgr = new PrivilegeManager(manageClient);

        initializeAppDeployer(new DeployRolesCommand(), new DeployPrivilegesCommand());
        appDeployer.deploy(appConfig);

        try {
            assertTrue(roleMgr.exists("sample-app-role1"));
            assertTrue(privMgr.exists("sample-app-execute-1"));
        } finally {
            undeploySampleApp();

            assertFalse(roleMgr.exists("sample-app-role1"));
            assertFalse(privMgr.exists("sample-app-execute-1"));
        }

    }
}
