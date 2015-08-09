package com.rjrudin.marklogic.appdeployer.command.tasks;

import com.rjrudin.marklogic.appdeployer.command.AbstractManageResourceTest;
import com.rjrudin.marklogic.appdeployer.command.Command;
import com.rjrudin.marklogic.appdeployer.command.tasks.CreateScheduledTasksCommand;
import com.rjrudin.marklogic.mgmt.ResourceManager;
import com.rjrudin.marklogic.mgmt.tasks.TaskManager;

public class ManageScheduledTasksTest extends AbstractManageResourceTest {

    @Override
    protected ResourceManager newResourceManager() {
        return new TaskManager(manageClient);
    }

    @Override
    protected Command newCommand() {
        return new CreateScheduledTasksCommand();
    }

    /**
     * The task path is used in the absence of a name for a task.
     */
    @Override
    protected String[] getResourceNames() {
        return new String[] { "/path/to/query.xqy" };
    }

}
