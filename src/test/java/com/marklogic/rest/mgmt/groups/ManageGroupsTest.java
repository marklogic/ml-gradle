package com.marklogic.rest.mgmt.groups;

import java.util.List;

import org.jdom2.Element;
import org.junit.Test;

import com.marklogic.appdeployer.command.AbstractManageResourceTest;
import com.marklogic.appdeployer.command.Command;
import com.marklogic.appdeployer.command.groups.CreateGroupsCommand;
import com.marklogic.appdeployer.command.security.CreateRolesCommand;
import com.marklogic.rest.mgmt.AbstractMgmtTest;
import com.marklogic.rest.mgmt.ResourceManager;
import com.marklogic.rest.mgmt.groups.GroupManager;
import com.marklogic.rest.mgmt.security.RoleManager;
import com.marklogic.rest.util.Fragment;
import com.marklogic.rest.util.ResourcesFragment;

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
		return new String[] { "new-group" };
	}
	
	@Test
    @Override
    public void afterResourcesCreated() {
        GroupManager mgr = new GroupManager(manageClient);
        Fragment f = mgr.getAsXml("new-group");
        assertTrue("The save call should either create or update a group",
                f.elementExists("/group-default-list/list-items/list-item/nameref[. = 'new-group']"));
    }
	
	
	@Test
	public void testDefaultGroup() {
      GroupManager mgr = new GroupManager(manageClient);
      Boolean exists = mgr.exists("Default");
      ResourcesFragment xml = mgr.getAsXml();
      assertTrue("The Default group should exist.", exists);
  }
}
