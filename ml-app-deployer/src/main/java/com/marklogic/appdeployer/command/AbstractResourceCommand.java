/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.appdeployer.command;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.mgmt.SaveReceipt;
import com.marklogic.mgmt.api.configuration.Configuration;
import com.marklogic.mgmt.resource.ResourceManager;

import java.io.File;
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
		final boolean isIncrementalDeploy = context.getAppConfig().isIncrementalDeploy();
		final boolean mergeResourcesBeforeSaving = resourceMergingIsSupported(context);

		if (mergeResourcesBeforeSaving) {
			logger.info("Will read and merge resource files in each config path before saving any resources");
			setIncrementalMode(false);
			if (isIncrementalDeploy) {
				logger.info("Incremental deploy will not be enabled since files are being read and merged first");
			}
		} else {
			setIncrementalMode(isIncrementalDeploy);
		}

		for (File resourceDir : getResourceDirs(context)) {
			processExecuteOnResourceDir(context, resourceDir);
		}

		if (mergeResourcesBeforeSaving) {
			List<ResourceReference> references = (List<ResourceReference>) context.getContextMap().get(getContextKeyForResourcesToSave());
			if (references != null && !references.isEmpty()) {
				List<ResourceReference> mergedReferences = mergeResources(references);
				if (useCmaForDeployingResources(context)) {
					saveMergedResourcesViaCma(context, mergedReferences);
				} else {
					saveMergedResources(context, getResourceManager(context), mergedReferences);
				}
			}
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
				} else {
					logResourceDirectoryNotFound(dir);
				}
			}
		} else {
			logger.warn("No ConfigDir objects found in AppConfig, unable to find resource directories");
		}
		return list.toArray(new File[]{});
	}

	/**
	 * Processes every valid file found in the given resource directory.
	 * <p>
	 * Starting in 3.11.0, if the subclass implements SupportsCmaCommand and CMA optimization is enabled in the
	 * AppConfig in the CommandContext, this method will deploy resources via a single CMA configuration.
	 *
	 * @param context
	 * @param resourceDir
	 */
	protected void processExecuteOnResourceDir(CommandContext context, File resourceDir) {
		if (resourceDir.exists()) {
			if (logger.isInfoEnabled()) {
				logger.info("Processing files in directory: " + resourceDir.getAbsolutePath());
			}

			// If resources should be merged, CMA will be used later to submit them all in one request, but not now
			if (useCmaForDeployingResources(context) && !resourceMergingIsSupported(context)) {
				if (logger.isInfoEnabled()) {
					logger.info("Command supports deployment via CMA, so will submit all resources via a single CMA configuration for directory: " + resourceDir);
				}
				deployResourcesViaCma(context, resourceDir);
			} else {
				ResourceManager mgr = getResourceManager(context);
				for (File resourceFile : listFilesInDirectory(resourceDir, context)) {
					if (logger.isInfoEnabled()) {
						logger.info("Processing file: " + resourceFile.getAbsolutePath());
					}
					SaveReceipt receipt = saveResource(mgr, context, resourceFile);
					afterResourceSaved(mgr, context, new ResourceReference(resourceFile, null), receipt);
				}
			}
		} else {
			logResourceDirectoryNotFound(resourceDir);
		}
	}

	/**
	 * If this command is an instance of SupportsCmaCommand, it first needs to specify whether CMA should be used - it
	 * is expected that a property is available in AppConfig to configure whether CMA should actually be used. If CMA
	 * should be used, a quick check is then made to ensure that the CMA endpoint exists.
	 *
	 * @param context
	 * @return
	 */
	protected boolean useCmaForDeployingResources(CommandContext context) {
		if (this instanceof SupportsCmaCommand) {
			SupportsCmaCommand command = (SupportsCmaCommand) this;
			if (command.cmaShouldBeUsed(context)) {
				return cmaEndpointExists(context);
			}
		}
		return false;
	}

	/**
	 * Depends on the subclass implementing SupportsCmaCommand.
	 *
	 * @param context
	 * @param resourceDir
	 */
	protected void deployResourcesViaCma(CommandContext context, File resourceDir) {
		Configuration config = new Configuration();
		for (File f : listFilesInDirectory(resourceDir, context)) {
			if (logger.isInfoEnabled()) {
				logger.info("Processing file: " + f.getAbsolutePath());
			}
			String payload = readResourceFromFile(context, f);
			if (payload != null && payload.trim().length() > 0) {
				ObjectNode objectNode = convertPayloadToObjectNode(context, payload);
				((SupportsCmaCommand) this).addResourceToConfiguration(objectNode, config);
			}
		}
		deployConfiguration(context, config);
	}

	/**
	 * @param context
	 * @param mergedReferences
	 */
	protected void saveMergedResourcesViaCma(CommandContext context, List<ResourceReference> mergedReferences) {
		Configuration config = new Configuration();
		for (ResourceReference reference : mergedReferences) {
			((SupportsCmaCommand) this).addResourceToConfiguration(reference.getObjectNode(), config);
		}
		deployConfiguration(context, config);
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
	 * For 3.14.0, resources won't be merged during an undo. This should only result in some unnecessary delete calls
	 * being made for resources that were already deleted.
	 *
	 * @param context
	 */
	@Override
	public void undo(CommandContext context) {
		if (deleteResourcesOnUndo) {
			setIncrementalMode(false);
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
		String payload = copyFileToString(f, context);
		final ResourceManager resourceManager = adjustResourceManagerForPayload(mgr, context, payload);

		final String finalPayload = adjustPayloadBeforeDeletingResource(resourceManager, context, f, payload);
		if (finalPayload == null) {
			return;
		}

		try {
			if (restartAfterDelete) {
				context.getAdminManager().invokeActionRequiringRestart(() -> resourceManager.delete(finalPayload).isDeleted());
			} else {
				resourceManager.delete(finalPayload);
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

	/**
	 * A subclass can override this to e.g. determine that, based on the payload, the resource should not be undeployed,
	 * in which case null should be returned.
	 *
	 * @param mgr
	 * @param context
	 * @param f
	 * @param payload
	 * @return
	 */
	protected String adjustPayloadBeforeDeletingResource(ResourceManager mgr, CommandContext context, File f, String payload) {
		return payload;
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
