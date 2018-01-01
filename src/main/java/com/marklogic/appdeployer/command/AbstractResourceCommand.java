package com.marklogic.appdeployer.command;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.mgmt.admin.AdminManager;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.SaveReceipt;
import com.marklogic.mgmt.admin.ActionRequiringRestart;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

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

	protected File[] findResourceDirs(CommandContext context, ResourceDirFinder resourceDirFinder) {
		return findResourceDirs(context.getAppConfig(), resourceDirFinder);
	}

	/**
	 * A subclass is likely to use this as a simple way of selecting all of the resource directories, specific to the
	 * subclass's resource, within each ConfigDir on the AppConfig.
	 *
	 * @param appConfig
	 * @param resourceDirFinder
	 * @return
	 */
	protected File[] findResourceDirs(AppConfig appConfig, ResourceDirFinder resourceDirFinder) {
		List<File> list = new ArrayList<>();
		List<ConfigDir> configDirs = appConfig.getConfigDirs();
		if (configDirs != null && !configDirs.isEmpty()) {
			for (ConfigDir configDir : appConfig.getConfigDirs()) {
				File dir = resourceDirFinder.getResourceDir(configDir);
				if (dir != null && dir.exists()) {
					list.add(dir);
				} else if (dir != null && logger.isInfoEnabled()) {
					logger.info("No resource directory found at: " + dir.getAbsolutePath());
				}
			}
		}
		else {
			logger.warn("No ConfigDir objects found in AppConfig, unable to find resource directories");
		}
		return list.toArray(new File[]{});
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
    protected void deleteResource(ResourceManager mgr, CommandContext context, File f) {
        final String payload = copyFileToString(f, context);
        final ResourceManager resourceManager = adjustResourceManagerForPayload(mgr, context, payload);
        try {
            if (restartAfterDelete) {
                context.getAdminManager().invokeActionRequiringRestart(new ActionRequiringRestart() {
                    @Override
                    public boolean execute() {
                        return resourceManager.delete(payload).isDeleted();
                    }
                });
            } else {
	            resourceManager.delete(payload);
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
