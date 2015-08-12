package com.rjrudin.marklogic.appdeployer.command;

import java.io.File;
import java.util.Arrays;

import com.rjrudin.marklogic.mgmt.ResourceManager;
import com.rjrudin.marklogic.mgmt.SaveReceipt;
import com.rjrudin.marklogic.mgmt.admin.ActionRequiringRestart;

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
            for (File f : listFilesInDirectory(resourceDir)) {
                if (isResourceFile(f)) {
                    SaveReceipt receipt = saveResource(mgr, context, f);
                    afterResourceSaved(mgr, context, f, receipt);
                }
            }
        }
    }

    protected File[] listFilesInDirectory(File dir) {
        File[] files = dir.listFiles();
        Arrays.sort(files);
        return files;
    }

    /**
     * Extracted for re-use.
     * 
     * @param mgr
     * @param context
     * @param f
     * @return
     */
    protected SaveReceipt saveResource(ResourceManager mgr, CommandContext context, File f) {
        String payload = copyFileToString(f);
        payload = tokenReplacer.replaceTokens(payload, context.getAppConfig(), false);
        return mgr.save(payload);
    }

    /**
     * Subclasses can override this to add functionality after a resource has been saved.
     * 
     * @param mgr
     * @param context
     * @param resourceFile
     * @param receipt
     */
    protected void afterResourceSaved(ResourceManager mgr, CommandContext context, File resourceFile,
            SaveReceipt receipt) {

    }

    @Override
    public void undo(CommandContext context) {
        if (deleteResourcesOnUndo) {
            File resourceDir = getResourcesDir(context);
            if (resourceDir.exists()) {
                final ResourceManager mgr = getResourceManager(context);
                for (File f : listFilesInDirectory(resourceDir)) {
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
