package com.marklogic.appdeployer.command;

import com.marklogic.mgmt.admin.AdminManager;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.SaveReceipt;
import com.marklogic.mgmt.admin.ActionRequiringRestart;
import com.marklogic.mgmt.admin.AdminManager;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.net.URI;

/**
 * Provides a basic implementation for creating/updating a resource while an app is being deployed and then deleting it
 * while the app is being undeployed.
 */
public abstract class AbstractResourceCommand extends AbstractUndoableCommand {

    private boolean deleteResourcesOnUndo = true;
    private boolean restartAfterDelete = false;
    private boolean catchExceptionOnDeleteFailure = false;

    protected abstract File[] getResourceDirs(CommandContext context);

    protected abstract ResourceManager getResourceManager(CommandContext context);

    @Override
    public void execute(CommandContext context) {
        for (File resourceDir : getResourceDirs(context)) {
            processExecuteOnResourceDir(context, resourceDir);
        }
    }

    protected void processExecuteOnResourceDir(CommandContext context, File resourceDir) {
        if (resourceDir.exists()) {
            ResourceManager mgr = getResourceManager(context);
            if (logger.isInfoEnabled()) {
                logger.info("Processing files in directory: " + resourceDir.getAbsolutePath());
            }
            for (File f : listFilesInDirectory(resourceDir, context)) {
                if (logger.isInfoEnabled()) {
                    logger.info("Processing file: " + f.getAbsolutePath());
                }
                SaveReceipt receipt = saveResource(mgr, context, f);
                afterResourceSaved(mgr, context, f, receipt);
            }
        }
    }

	/**
	 * Defaults to the parent method. This was extracted so that a subclass can override it and have access to the
	 * CommandContext, which allows for reading in the contents of each file and replacing tokens, which may impact the
	 * order in which the files are processed.
	 *
	 * @param resourceDir
	 * @param context
	 * @return
	 */
	protected File[] listFilesInDirectory(File resourceDir, CommandContext context) {
    	return listFilesInDirectory(resourceDir);
    }

    /**
     * Subclasses can override this to add functionality after a resource has been saved.
     *
     * Starting in version 3.0 of ml-app-deployer, this will always check if the Location header is
     * /admin/v1/timestamp, and if so, it will wait for ML to restart.
     *
     * @param mgr
     * @param context
     * @param resourceFile
     * @param receipt
     */
    protected void afterResourceSaved(ResourceManager mgr, CommandContext context, File resourceFile,
            SaveReceipt receipt) {
    	ResponseEntity<String> response = receipt.getResponse();
    	if (response != null) {
    		HttpHeaders headers = response.getHeaders();
    		if (headers != null) {
			    URI uri = headers.getLocation();
			    if (uri != null && "/admin/v1/timestamp".equals(uri.getPath())) {
				    AdminManager adminManager = context.getAdminManager();
				    if (adminManager != null) {
					    adminManager.waitForRestart();
				    } else {
					    logger.warn("Location header indicates ML is restarting, but no AdminManager available to support waiting for a restart");
				    }
			    }
		    }
	    }
    }

    @Override
    public void undo(CommandContext context) {
        if (deleteResourcesOnUndo) {
            for (File resourceDir : getResourceDirs(context)) {
                processUndoOnResourceDir(context, resourceDir);
            }
        }
    }

    protected void processUndoOnResourceDir(CommandContext context, File resourceDir) {
        if (resourceDir.exists()) {
            if (logger.isInfoEnabled()) {
                logger.info("Processing files in directory: " + resourceDir.getAbsolutePath());
            }
            final ResourceManager mgr = getResourceManager(context);
            for (File f : listFilesInDirectory(resourceDir)) {
                if (logger.isInfoEnabled()) {
                    logger.info("Processing file: " + f.getAbsolutePath());
                }
                deleteResource(mgr, context, f);
            }
        }
    }

    /**
     * If catchExceptionOnDeleteFailure is set to true, this will catch and log any exception that occurs when trying to
     * delete the resource. This has been necessary when deleting two app servers in a row - for some reason, the 2nd
     * delete will intermittently fail with a connection reset error, but the app server is in fact deleted
     * successfully.
     *
     * @param mgr
     * @param context
     * @param f
     */
    protected void deleteResource(final ResourceManager mgr, CommandContext context, File f) {
        final String payload = copyFileToString(f, context);
        try {
            if (restartAfterDelete) {
                context.getAdminManager().invokeActionRequiringRestart(new ActionRequiringRestart() {
                    @Override
                    public boolean execute() {
                        return mgr.delete(payload).isDeleted();
                    }
                });
            } else {
                mgr.delete(payload);
            }
        } catch (RuntimeException e) {
            if (catchExceptionOnDeleteFailure) {
                logger.warn("Caught exception while trying to delete resource; cause: " + e.getMessage());
                if (restartAfterDelete) {
                    context.getAdminManager().waitForRestart();
                }
            } else {
                throw e;
            }
        }
    }

    public void setDeleteResourcesOnUndo(boolean deleteResourceOnUndo) {
        this.deleteResourcesOnUndo = deleteResourceOnUndo;
    }

    public void setRestartAfterDelete(boolean restartAfterDelete) {
        this.restartAfterDelete = restartAfterDelete;
    }

    public boolean isDeleteResourcesOnUndo() {
        return deleteResourcesOnUndo;
    }

    public boolean isRestartAfterDelete() {
        return restartAfterDelete;
    }

    public void setCatchExceptionOnDeleteFailure(boolean catchExceptionOnDeleteFailure) {
        this.catchExceptionOnDeleteFailure = catchExceptionOnDeleteFailure;
    }
}
