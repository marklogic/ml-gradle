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
package com.marklogic.appdeployer.command.triggers;

import com.marklogic.appdeployer.command.AbstractManageResourceTest;
import com.marklogic.appdeployer.command.Command;
import com.marklogic.appdeployer.command.databases.DeployOtherDatabasesCommand;
import com.marklogic.mgmt.api.API;
import com.marklogic.mgmt.api.trigger.Permission;
import com.marklogic.mgmt.api.trigger.Trigger;
import com.marklogic.mgmt.mapper.DefaultResourceMapper;
import com.marklogic.mgmt.mapper.ResourceMapper;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.triggers.TriggerManager;

import static org.junit.jupiter.api.Assertions.*;

public class ManageTriggersTest extends AbstractManageResourceTest {

    @Override
    protected void initializeAndDeploy() {
        initializeAppDeployer(new DeployOtherDatabasesCommand(1), newCommand());
        appDeployer.deploy(appConfig);
    }

    @Override
    protected ResourceManager newResourceManager() {
        return new TriggerManager(manageClient, appConfig.getTriggersDatabaseName());
    }

    @Override
    protected Command newCommand() {
        return new DeployTriggersCommand();
    }

    @Override
    protected String[] getResourceNames() {
        return new String[] { "my-trigger" };
    }

	/**
	 * Added the Trigger class in 3.14.0, doing some basic validation of it here.
	 */
	@Override
	protected void afterResourcesCreated() {
		ResourceMapper mapper = new DefaultResourceMapper(new API(manageClient));

    	String json = newResourceManager().getPropertiesAsJson("my-trigger");
		verifyTriggerProperties(mapper.readResource(json, Trigger.class));

		String xml = newResourceManager().getPropertiesAsXmlString("my-trigger");
		verifyTriggerProperties(mapper.readResource(xml, Trigger.class));
	}

	private void verifyTriggerProperties(Trigger t) {
		assertNotNull(t.getId());
		assertEquals("my-trigger", t.getName());
		assertEquals("my trigger", t.getDescription());
		assertEquals("/myDir/", t.getEvent().getDataEvent().getDirectoryScope().getUri());
		assertEquals("1", t.getEvent().getDataEvent().getDirectoryScope().getDepth());
		assertEquals("create", t.getEvent().getDataEvent().getDocumentContent().getUpdateKind());
		assertEquals("post-commit", t.getEvent().getDataEvent().getWhen());
		assertEquals("/test.xqy", t.getModule());
		assertEquals("Modules", t.getModuleDb());
		assertEquals("/modules/", t.getModuleRoot());
		assertTrue(t.getEnabled());
		assertTrue(t.getRecursive());
		assertEquals("normal", t.getTaskPriority());

		// Order isn't guaranteed, so check them all
		boolean foundPermission = false;
		for (Permission p : t.getPermission()) {
			if ("trigger-management".equals(p.getRoleName()) && "update".equals(p.getCapability())) {
				foundPermission = true;
				break;
			}
		}
		assertTrue(foundPermission, "Did not find trigger-management/update permission");
	}

	/**
     * No need to do anything here, as the triggers are deleted when the triggers database is deleted.
     */
    @Override
    protected void verifyResourcesWereDeleted(ResourceManager mgr) {
    }
}
