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
package com.marklogic.appdeployer.command.tasks;

import com.marklogic.appdeployer.command.groups.DeployGroupsCommand;
import org.junit.jupiter.api.Test;

import com.marklogic.appdeployer.command.AbstractManageResourceTest;
import com.marklogic.appdeployer.command.Command;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.tasks.TaskManager;
import com.marklogic.rest.util.ResourcesFragment;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ManageScheduledTasksTest extends AbstractManageResourceTest {

    @Override
    protected ResourceManager newResourceManager() {
        return new TaskManager(manageClient);
    }

    @Override
    protected Command newCommand() {
        return new DeployScheduledTasksCommand();
    }

    /**
     * The task path is used in the absence of a name for a task.
     */
    @Override
    protected String[] getResourceNames() {
        return new String[] { "/path/to/query.xqy" };
    }

    @Test
    public void deleteAll() {
        initializeAndDeploy();
        TaskManager mgr = new TaskManager(manageClient);
        mgr.deleteAllScheduledTasks();

        ResourcesFragment frag = mgr.getAsXml();
        assertEquals(0, frag.getListItemIdRefs().size(), "All of the scheduled tasks should have been deleted");
    }

    @Test
    public void associateChildDirectoryWithGroup() {
	    appConfig.getFirstConfigDir().setBaseDir(new File("src/test/resources/sample-app/tasks-in-child-dir"));
    	initializeAppDeployer(new DeployGroupsCommand(), new DeployScheduledTasksCommand());
    	try {
		    appDeployer.deploy(appConfig);

		    TaskManager taskManager = new TaskManager(manageClient, "sampleAppGroup1");
		    ResourcesFragment tasks = taskManager.getAsXml();
		    assertEquals(1, tasks.getResourceCount(), "Should have 1 task for our test group");
		    String taskId = tasks.getListItemIdRefs().get(0);
		    assertEquals("/path/to/other-query.xqy", tasks.getListItemValue(taskId, "task-path"));
	    } finally {
    		undeploySampleApp();
	    }
    }

}
