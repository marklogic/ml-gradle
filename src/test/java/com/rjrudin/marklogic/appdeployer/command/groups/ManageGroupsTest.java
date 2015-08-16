package com.rjrudin.marklogic.appdeployer.command.groups;

import com.rjrudin.marklogic.appdeployer.command.AbstractManageResourceTest;
import com.rjrudin.marklogic.appdeployer.command.Command;
import com.rjrudin.marklogic.appdeployer.command.groups.DeployGroupsCommand;
import com.rjrudin.marklogic.mgmt.ResourceManager;
import com.rjrudin.marklogic.mgmt.groups.GroupManager;
import com.rjrudin.marklogic.rest.util.Fragment;

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
        assertEquals("metering should be turned off as configured in sample-app-group.json", "false",
                f.getElementValue("/m:group-properties/m:metering-enabled"));
    }

}
