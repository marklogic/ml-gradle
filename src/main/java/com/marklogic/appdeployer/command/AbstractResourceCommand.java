package com.marklogic.appdeployer.command;

import java.io.File;

import com.marklogic.rest.mgmt.ResourceManager;

/**
 * Provides a basic implementation for creating/updating a resource while an app is being deployed and then deleting it
 * while the app is being undeployed.
 */
public abstract class AbstractResourceCommand extends AbstractCommand implements UndoableCommand {

    private boolean deleteResourcesOnUndo = true;

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
                if (f.getName().endsWith(".json")) {
                    mgr.save(copyFileToString(f));
                }
            }
        }
    }

    @Override
    public void undo(CommandContext context) {
        if (deleteResourcesOnUndo) {
            File resourceDir = getResourcesDir(context);
            if (resourceDir.exists()) {
                ResourceManager mgr = getResourceManager(context);
                for (File f : resourceDir.listFiles()) {
                    if (f.getName().endsWith(".json")) {
                        mgr.delete(copyFileToString(f));
                    }
                }
            }
        }
    }

    public void setDeleteResourcesOnUndo(boolean deleteResourceOnUndo) {
        this.deleteResourcesOnUndo = deleteResourceOnUndo;
    }
}
