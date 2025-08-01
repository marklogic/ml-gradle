/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
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
