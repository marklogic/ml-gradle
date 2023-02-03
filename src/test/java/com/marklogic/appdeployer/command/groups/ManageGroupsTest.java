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
package com.marklogic.appdeployer.command.groups;

import com.marklogic.appdeployer.command.AbstractManageResourceTest;
import com.marklogic.appdeployer.command.Command;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.groups.GroupManager;
import com.marklogic.rest.util.Fragment;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ManageGroupsTest extends AbstractManageResourceTest {

    @Override
    protected ResourceManager newResourceManager() {
        return new GroupManager(manageClient);
    }

    @Override
    protected Command newCommand() {
        return new DeployGroupsCommand();
    }

    @Override
    protected String[] getResourceNames() {
        return new String[] { "sample-app-group" };
    }

    @Override
    protected void afterResourcesCreated() {
        GroupManager mgr = new GroupManager(manageClient);
        Fragment f = mgr.getPropertiesAsXml("sample-app-group");
        assertEquals("false", f.getElementValue("/m:group-properties/m:metering-enabled"),
			"metering should be turned off as configured in sample-app-group.json");
    }

}
