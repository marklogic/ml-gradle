package com.marklogic.appdeployer.command.groups;

import java.io.File;

import com.marklogic.appdeployer.command.AbstractResourceCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.rest.mgmt.ResourceManager;
import com.marklogic.rest.mgmt.groups.GroupManager;

public class CreateGroupsCommand extends AbstractResourceCommand {

	@Override
	public void execute(CommandContext context) {
		// TODO Auto-generated method stub
		
	}
    protected File getResourcesDir(CommandContext context) {
        return new File(context.getAppConfig().getConfigDir().getSecurityDir(), "groups");
    }

    @Override
    protected ResourceManager getResourceManager(CommandContext context) {
        return new GroupManager(context.getManageClient());
    }

}
