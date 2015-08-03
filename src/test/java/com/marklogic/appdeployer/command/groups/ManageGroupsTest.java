package com.marklogic.appdeployer.command.groups;

import com.marklogic.appdeployer.command.AbstractManageResourceTest;
import com.marklogic.appdeployer.command.Command;
import com.marklogic.rest.mgmt.ResourceManager;
import com.marklogic.rest.mgmt.groups.GroupManager;

public class ManageGroupsTest extends AbstractManageResourceTest {

    @Override
    protected ResourceManager newResourceManager() {
        return new GroupManager(manageClient);
    }

    @Override
    protected Command newCommand() {
        return new CreateGroupsCommand();
    }

    @Override
    protected String[] getResourceNames() {
        return new String[] { "sample-app-group" };
    }

}
