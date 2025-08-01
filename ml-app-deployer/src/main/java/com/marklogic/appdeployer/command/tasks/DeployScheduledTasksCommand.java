/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.command.tasks;

import com.marklogic.appdeployer.command.AbstractResourceCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.tasks.TaskManager;

import java.io.File;
import java.io.FileFilter;

public class DeployScheduledTasksCommand extends AbstractResourceCommand {

    private String groupName;

    public DeployScheduledTasksCommand() {
        setExecuteSortOrder(SortOrderConstants.DEPLOY_SCHEDULED_TASKS);
        setUndoSortOrder(SortOrderConstants.DELETE_SCHEDULED_TASKS);
    }

    @Override
    protected File[] getResourceDirs(CommandContext context) {
    	return findResourceDirs(context, configDir -> configDir.getTasksDir());
    }

    @Override
    protected ResourceManager getResourceManager(CommandContext context) {
        TaskManager mgr = new TaskManager(context.getManageClient());
        if (groupName != null) {
            mgr.setGroupName(groupName);
        }
        return mgr;
    }

	/**
	 * We do some extra work here to process each child directory, where the name of the child directory is assumed to
	 * be a MarkLogic group name. This requires changing the state of this command momentarily, as we change the
	 * groupName of this command for each child directory, and then restore it to the original groupName.
	 *
	 * @param context
	 */
	@Override
    public void execute(CommandContext context) {
        super.execute(context);

        String originalGroupName = this.groupName;
        for (File resourceDir : getResourceDirs(context)) {
        	if (resourceDir != null && resourceDir.isDirectory()) {
				File[] dirs = resourceDir.listFiles(new IsDirectoryFilter());
				if (dirs != null) {
					for (File dir : dirs) {
						setGroupName(dir.getName());
						processExecuteOnResourceDir(context, dir);
					}
				}
	        }
        }
        setGroupName(originalGroupName);
    }

	/**
	 * Just like on execute, we do some extra work here to delete any tasks in child directories. This requires
	 * changing the state of this command momentarily, as we change the groupName of this command for each child
	 * directory, and then restore it to the original groupName.
	 *
	 * @param context
	 */
	@Override
    public void undo(CommandContext context) {
        super.undo(context);

		String originalGroupName = this.groupName;
		for (File resourceDir : getResourceDirs(context)) {
			if (resourceDir != null && resourceDir.isDirectory()) {
				File[] dirs = resourceDir.listFiles(new IsDirectoryFilter());
				if (dirs != null) {
					for (File dir : dirs) {
						setGroupName(dir.getName());
						processUndoOnResourceDir(context, dir);
					}
				}
			}
		}
		setGroupName(originalGroupName);
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}

class IsDirectoryFilter implements FileFilter {
	@Override
	public boolean accept(File pathname) {
		return pathname != null && pathname.isDirectory();
	}
}
