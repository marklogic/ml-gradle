package com.rjrudin.marklogic.appdeployer.command;

import java.io.File;
import java.net.URI;
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
    private boolean storeResourceIdsAsCustomTokens = false;
    private int undoSortOrder = Integer.MAX_VALUE;

    protected abstract File getResourcesDir(CommandContext context);

    protected abstract ResourceManager getResourceManager(CommandContext context);

    @Override
    public Integer getUndoSortOrder() {
        return undoSortOrder;
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
        SaveReceipt receipt = mgr.save(payload);
        if (storeResourceIdsAsCustomTokens) {
            storeTokenForResourceId(receipt, context);
        }
        return receipt;
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

    /**
     * Any resource that may be referenced by its ID by another resource will most likely need its ID stored as a custom
     * token so that it can be referenced by the other resource. To enable this, the subclass should set
     * storeResourceIdAsCustomToken to true.
     * 
     * @param receipt
     * @param context
     */
    protected void storeTokenForResourceId(SaveReceipt receipt, CommandContext context) {
        URI location = receipt.getResponse().getHeaders().getLocation();

        String idValue = null;
        String resourceName = null;

        if (location != null) {
            String[] tokens = location.getPath().split("/");
            idValue = tokens[tokens.length - 1];
            resourceName = tokens[tokens.length - 2];
        } else {
            String[] tokens = receipt.getPath().split("/");
            // Path is expected to end in /(resources-name)/(id)/properties
            idValue = tokens[tokens.length - 2];
            resourceName = tokens[tokens.length - 3];
        }

        String key = "%%" + resourceName + "-id-" + receipt.getResourceId() + "%%";
        if (logger.isInfoEnabled()) {
            logger.info(format("Storing token with key '%s' and value '%s'", key, idValue));
        }

        context.getAppConfig().getCustomTokens().put(key, idValue);
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

    public void setStoreResourceIdsAsCustomTokens(boolean storeResourceIdsAsCustomTokens) {
        this.storeResourceIdsAsCustomTokens = storeResourceIdsAsCustomTokens;
    }

    public boolean isDeleteResourcesOnUndo() {
        return deleteResourcesOnUndo;
    }

    public boolean isRestartAfterDelete() {
        return restartAfterDelete;
    }

    public boolean isStoreResourceIdsAsCustomTokens() {
        return storeResourceIdsAsCustomTokens;
    }

    public void setUndoSortOrder(int undoSortOrder) {
        this.undoSortOrder = undoSortOrder;
    }
}
