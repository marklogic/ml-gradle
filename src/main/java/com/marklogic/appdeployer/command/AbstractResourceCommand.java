package com.marklogic.appdeployer.command;

import java.io.File;

import com.marklogic.rest.mgmt.ResourceManager;
import com.marklogic.rest.mgmt.admin.ActionRequiringRestart;

/**
 * Provides a basic implementation for creating/updating a resource while an app is being deployed and then deleting it
 * while the app is being undeployed.
 */
public abstract class AbstractResourceCommand extends AbstractCommand implements UndoableCommand {

    private boolean deleteResourcesOnUndo = true;
    private boolean restartAfterDelete = false;

    protected abstract File getResourcesDir(CommandContext context);

    protected abstract ResourceManager getResourceManager(CommandContext context);

    @Override
    public Integer getUndoSortOrder() {
        return getExecuteSortOrder();
    }

    @Override
    public void execute(CommandContext context) {
        File resourceDir = getResourcesDir(context);
        if (resourceDir.exists()) {
            ResourceManager mgr = getResourceManager(context);
            for (File f : resourceDir.listFiles()) {
                if (isResourceFile(f)) {
                    saveResource(mgr, context, f);
                }
            }
        }
    }

    /**
     * This is in a protected method so that a subclass can easily override it to customize it or provide additional
     * functionality after a resource has been saved.
     * 
     * @param mgr
     * @param context
     * @param f
     * @return the payload that was sent to the ResourceManager
     */
    protected String saveResource(ResourceManager mgr, CommandContext context, File f) {
        String payload = copyFileToString(f);
        payload = tokenReplacer.replaceTokens(payload, context.getAppConfig(), false);
        mgr.save(payload);
        return payload;
    }

    @Override
    public void undo(CommandContext context) {
        if (deleteResourcesOnUndo) {
            File resourceDir = getResourcesDir(context);
            if (resourceDir.exists()) {
                final ResourceManager mgr = getResourceManager(context);
                for (File f : resourceDir.listFiles()) {
                    if (isResourceFile(f)) {
                        deleteResource(mgr, context, f);
                    }
                }
            }
        }
    }

    protected void deleteResource(final ResourceManager mgr, CommandContext context, File f) {
        final String payload = tokenReplacer.replaceTokens(copyFileToString(f), context.getAppConfig(), false);
        if (restartAfterDelete) {
            context.getAdminManager().invokeActionRequiringRestart(new ActionRequiringRestart() {
                @Override
                public boolean execute() {
                    return mgr.delete(payload);
                }
            });
        } else {
            mgr.delete(payload);
        }
    }

    public void setDeleteResourcesOnUndo(boolean deleteResourceOnUndo) {
        this.deleteResourcesOnUndo = deleteResourceOnUndo;
    }

    public void setRestartAfterDelete(boolean restartAfterDelete) {
        this.restartAfterDelete = restartAfterDelete;
    }
}
