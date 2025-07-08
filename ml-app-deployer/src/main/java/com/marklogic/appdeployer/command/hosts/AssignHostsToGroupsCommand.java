/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.command.hosts;

import java.util.Map;

import com.marklogic.appdeployer.command.AbstractUndoableCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.resource.hosts.HostManager;

public class AssignHostsToGroupsCommand extends AbstractUndoableCommand {

	private static final String DEFAULT_GROUP_NAME = "Default";

	public AssignHostsToGroupsCommand() {
        setExecuteSortOrder(SortOrderConstants.ASSIGN_HOSTS_TO_GROUPS);
        setUndoSortOrder(SortOrderConstants.UNASSIGN_HOSTS_FROM_GROUPS);
    }

	@Override
	public void execute(CommandContext context) {
		context.getAdminManager().invokeActionRequiringRestart(() -> assignHostsToGroups(context));
	}

	protected boolean assignHostsToGroups(CommandContext context) {
		boolean requiresRestart = false;

		Map<String, String> hostGroups = context.getAppConfig().getHostGroups();
		HostManager hostMgr = new HostManager(context.getManageClient());
		if (hostGroups != null) {
			for (Map.Entry<String, String> entry : hostGroups.entrySet()) {
				String hostName = entry.getKey();
				String groupName = entry.getValue();

				if (!groupName.equals(hostMgr.getAssignedGroupName(hostName))) {
					if (logger.isInfoEnabled()) {
						logger.info(format("Assigning host %s to group %s", hostName, groupName));
					}
					hostMgr.setHostToGroup(hostName, groupName);
					requiresRestart = true;
				}
			}
		}

		return requiresRestart;
	}

	@Override
	public void undo(CommandContext context) {
		context.getAdminManager().invokeActionRequiringRestart(() -> assignHostsToDefault(context));
	}

	protected boolean assignHostsToDefault(CommandContext context) {
		boolean requiresRestart = false;

		Map<String, String> hostGroups = context.getAppConfig().getHostGroups();
		if (hostGroups != null) {
			HostManager hostMgr = new HostManager(context.getManageClient());
			for (Map.Entry<String, String> entry : hostGroups.entrySet()) {
				String hostName = entry.getKey();
				if (!DEFAULT_GROUP_NAME.equals(hostMgr.getAssignedGroupName(hostName))) {
					if (logger.isInfoEnabled()) {
						logger.info(format("Assigning host %s to group %s", hostName, DEFAULT_GROUP_NAME));
					}
					hostMgr.setHostToGroup(hostName, DEFAULT_GROUP_NAME);
					requiresRestart = true;
				}
			}
		}

		return requiresRestart;
	}
}
