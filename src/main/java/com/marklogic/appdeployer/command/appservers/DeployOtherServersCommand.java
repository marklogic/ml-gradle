package com.marklogic.appdeployer.command.appservers;

import com.marklogic.appdeployer.command.AbstractResourceCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.ResourceFilenameFilter;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.PayloadParser;
import com.marklogic.mgmt.SaveReceipt;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.appservers.ServerManager;
import com.marklogic.mgmt.util.ObjectMapperFactory;

import java.io.File;

/**
 * "Other" = non-REST-API servers. This will process every JSON/XML file that's not named "rest-api-server.*" in the
 * servers directory.
 */
public class DeployOtherServersCommand extends AbstractResourceCommand {

    public DeployOtherServersCommand() {
        setExecuteSortOrder(SortOrderConstants.DEPLOY_OTHER_SERVERS);
        setUndoSortOrder(SortOrderConstants.DELETE_OTHER_SERVERS);
        setRestartAfterDelete(true);
        setCatchExceptionOnDeleteFailure(true);
        setResourceFilenameFilter(new ResourceFilenameFilter("rest-api-server.xml", "rest-api-server.json"));
    }

    @Override
    protected File[] getResourceDirs(CommandContext context) {
    	return findResourceDirs(context.getAppConfig(), configDir -> configDir.getServersDir());
    }

    @Override
    protected ResourceManager getResourceManager(CommandContext context) {
        return new ServerManager(context.getManageClient(), context.getAppConfig().getGroupName());
    }

    @Override
    public Integer getUndoSortOrder() {
        return 0;
    }

	/**
	 * If the payload has a group-name that differs from the group name in the AppConfig, then this returns a new
	 * ServerManager using the group-name in the payload.
	 *
	 * @param mgr
	 * @param context
	 * @param payload
	 * @return
	 */
	@Override
	protected ResourceManager adjustResourceManagerForPayload(ResourceManager mgr, CommandContext context, String payload) {
		String groupName = new PayloadParser().getPayloadFieldValue(payload, "group-name", false);
		if (groupName != null && !groupName.equals(context.getAppConfig().getGroupName())) {
			return new ServerManager(context.getManageClient(), groupName);
		}
		return mgr;
	}
}
