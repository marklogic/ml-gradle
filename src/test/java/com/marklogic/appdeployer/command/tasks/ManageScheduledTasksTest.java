package com.marklogic.appdeployer.command.tasks;

import org.junit.Test;

import com.marklogic.appdeployer.command.AbstractManageResourceTest;
import com.marklogic.appdeployer.command.Command;
import com.marklogic.mgmt.ResourceManager;
import com.marklogic.mgmt.tasks.TaskManager;
import com.marklogic.rest.util.ResourcesFragment;

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
        assertEquals("All of the scheduled tasks should have been deleted", 0, frag.getListItemIdRefs().size());
    }
}
