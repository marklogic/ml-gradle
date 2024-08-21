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
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.client.ext.helper.LoggingObject;
import com.marklogic.mgmt.PayloadParser;
import com.marklogic.mgmt.SaveReceipt;
import com.marklogic.mgmt.admin.AdminManager;
import com.marklogic.mgmt.api.API;
import com.marklogic.mgmt.api.Resource;
import com.marklogic.mgmt.api.configuration.Configuration;
import com.marklogic.mgmt.api.configuration.Configurations;
import com.marklogic.mgmt.cma.ConfigurationManager;
import com.marklogic.mgmt.mapper.DefaultResourceMapper;
import com.marklogic.mgmt.mapper.ResourceMapper;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.databases.DatabaseManager;
import com.marklogic.mgmt.util.ObjectMapperFactory;
import com.marklogic.rest.util.JsonNodeUtil;
import com.marklogic.rest.util.PropertyBasedBiPredicate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.regex.Pattern;

/**
 * Abstract base class that provides some convenience methods for implementing a command. Subclasses will typically
 * override the default sort order within the subclass constructor.
 */
public abstract class AbstractCommand extends LoggingObject implements Command {

	private int executeSortOrder = Integer.MAX_VALUE;
	private boolean storeResourceIdsAsCustomTokens = false;

	protected PayloadTokenReplacer payloadTokenReplacer = new DefaultPayloadTokenReplacer();
	private FilenameFilter resourceFilenameFilter = new ResourceFilenameFilter();
	private PayloadParser payloadParser = new PayloadParser();

	private Class<? extends Resource> resourceClassType;
	private String resourceIdPropertyName;
	private ResourceMapper resourceMapper;
	private boolean supportsResourceMerging = false;

	/**
	 * A subclass can set the executeSortOrder attribute to whatever value it needs.
	 */
	@Override
	public Integer getExecuteSortOrder() {
		return this.executeSortOrder;
	}

	/**
	 * Convenience method for setting the names of files to ignore when reading resources from a directory. Will
	 * preserve any filenames already being ignored on the underlying FilenameFilter.
	 *
	 * @param filenames
	 */
	public void setFilenamesToIgnore(String... filenames) {
		if (filenames == null || filenames.length == 0) {
			return;
		}
		if (resourceFilenameFilter != null) {
			if (resourceFilenameFilter instanceof ResourceFilenameFilter) {
				ResourceFilenameFilter rff = (ResourceFilenameFilter) resourceFilenameFilter;
				Set<String> set = null;
				if (rff.getFilenamesToIgnore() != null) {
					set = rff.getFilenamesToIgnore();
				} else {
					set = new HashSet<>();
				}
				set.addAll(Arrays.asList(filenames));
				rff.setFilenamesToIgnore(set);
			} else {
				logger.warn("resourceFilenameFilter is not an instanceof ResourceFilenameFilter, so unable to set resource filenames to ignore");
			}
		} else {
			this.resourceFilenameFilter = new ResourceFilenameFilter(filenames);
		}
	}

	public void setResourceFilenamesExcludePattern(Pattern pattern) {
		if (resourceFilenameFilter != null) {
			if (resourceFilenameFilter instanceof ResourceFilenameFilter) {
				((ResourceFilenameFilter) resourceFilenameFilter).setExcludePattern(pattern);
			} else {
				logger.warn("resourceFilenameFilter is not an instanceof ResourceFilenameFilter, so unable to set exclude pattern");
			}
		} else {
			ResourceFilenameFilter rff = new ResourceFilenameFilter();
			rff.setExcludePattern(pattern);
			this.resourceFilenameFilter = rff;
		}
	}

	public void setResourceFilenamesIncludePattern(Pattern pattern) {
		if (resourceFilenameFilter != null) {
			if (resourceFilenameFilter instanceof ResourceFilenameFilter) {
				((ResourceFilenameFilter) resourceFilenameFilter).setIncludePattern(pattern);
			} else {
				logger.warn("resourceFilenameFilter is not an instanceof ResourceFilenameFilter, so unable to set include pattern");
			}
		} else {
			ResourceFilenameFilter rff = new ResourceFilenameFilter();
			rff.setIncludePattern(pattern);
			this.resourceFilenameFilter = rff;
		}
	}

	/**
	 * Simplifies reading the contents of a File into a String.
	 *
	 * @param f
	 * @return
	 */
	protected String copyFileToString(File f) {
		try {
			File absoluteFile = f.getAbsoluteFile();
			if (logger.isDebugEnabled()) {
				logger.debug("Copying content from absolute file path: " + absoluteFile.getPath() + "; input file path: " + f.getPath());
			}
			return new String(FileCopyUtils.copyToByteArray(f.getAbsoluteFile()));
		} catch (IOException ie) {
			throw new RuntimeException(
				"Unable to copy file to string from path: " + f.getAbsolutePath() + "; cause: " + ie.getMessage(),
				ie);
		}
	}

	/**
	 * Convenience function for reading the file into a string and replace tokens as well. Assumes this is not
	 * for a test-only resource.
	 *
	 * @param f
	 * @param context
	 * @return
	 */
	protected String copyFileToString(File f, CommandContext context) {
		String str = copyFileToString(f);
		return str != null ? payloadTokenReplacer.replaceTokens(str, context.getAppConfig(), false) : str;
	}

	/**
	 * Provides a basic implementation for saving a resource defined in a File, including replacing tokens.
	 * <p>
	 * New in 3.14.0 - if mergeResourcesBeforeSaving is set to true, this will not save the resource and will return
	 * null. It will instead read the payload from the file, convert it to JSON if it's XML, and then store it
	 * so it can be merged and saved after all files have been read.
	 *
	 * @param mgr
	 * @param context
	 * @param resourceFile
	 * @return
	 */
	protected SaveReceipt saveResource(ResourceManager mgr, CommandContext context, File resourceFile) {
		String payload = readResourceFromFile(context, resourceFile);

		if (payload != null && resourceMergingIsSupported(context)) {
			try {
				storeResourceInCommandContextMap(context, resourceFile, payload);
				return null;
			} catch (Exception ex) {
				/**
				 * As a worst case, if the payload cannot be unmarshalled (and converted if necessary) into an ObjectNode,
				 * this warning will be logged and the resource will be saved immediately.
				 */
				logger.warn("Unable to store resource in context map so it can be merged (if needed) and " +
					"saved later, so the resource will instead be saved immediately. Error cause: " + ex.getMessage());
			}
		}

		return saveResource(mgr, context, payload);
	}

	/**
	 * For the 3.14.0 release, whether resource merging is enabled for a particular command depends on if it's enabled
	 * in the AppConfig object, and if the particular command is configured to support resource merging as well. This
	 * allows this feature to be gradually rolled out for each resource type, while also providing a way to turn it
	 * off completely at the AppConfig level.
	 *
	 * @param context
	 * @return
	 */
	protected boolean resourceMergingIsSupported(CommandContext context) {
		return supportsResourceMerging && context.getAppConfig().isMergeResources();
	}

	/**
	 * When this command is configured to merge resources before saving, resources read from files need to be stashed
	 * somewhere until they've all been read and can be merged together. This method handles converting a payload into
	 * an ObjectNode, which is the preferred data structure for merging resources together, and then stashing that
	 * ObjectNode in the CommandContext map.
	 *
	 */
	protected void storeResourceInCommandContextMap(CommandContext context, File resourceFile, String payload) {
		final String contextKey = getContextKeyForResourcesToSave();
		List<ResourceReference> references = (List<ResourceReference>) context.getContextMap().get(contextKey);
		if (references == null) {
			references = new ArrayList<>();
			context.getContextMap().put(contextKey, references);
		}
		references.add(new ResourceReference(resourceFile, convertPayloadToObjectNode(context, payload)));
	}

	/**
	 * When this command is configured to merge resources before saving, resources read from files need to be stashed
	 * somewhere until they've all been read and can be merged together. This method generates what should be a
	 * resource/command-specific key for stashing those resources in the CommandContext map.
	 *
	 * @return
	 */
	protected String getContextKeyForResourcesToSave() {
		return getClass().getName() + "-resources-to-save";
	}

	/**
	 * If the payload is XML, this will first convert it to JSON.
	 *
	 * @param context
	 * @param payload
	 * @return
	 */
	protected ObjectNode convertPayloadToObjectNode(CommandContext context, String payload) {
		payload = convertXmlPayloadToJsonIfNecessary(context, payload);
		try {
			return (ObjectNode) ObjectMapperFactory.getObjectMapper().readTree(payload);
		} catch (IOException e) {
			throw new RuntimeException("Unable to read JSON into an ObjectNode, cause: " + e.getMessage(), e);
		}
	}

	/**
	 * When merging resources, all payloads need to be converted into JSON so that ObjectNode's can be easily merged
	 * together. Thus for an XML payload, need to map it to a resource object first, and then get JSON from that resource
	 * object.
	 * <p>
	 * Note that this puts a burden on the resource objects being up-to-date with the Manage API schemas.
	 *
	 * @param context
	 * @param payload
	 * @return
	 */
	protected String convertXmlPayloadToJsonIfNecessary(CommandContext context, String payload) {
		if (payloadParser.isJsonPayload(payload)) {
			return payload;
		}

		if (resourceClassType == null) {
			throw new IllegalStateException("Cannot convert an XML payload to JSON because resourceClassType is not defined");
		}
		if (resourceMapper == null) {
			resourceMapper = new DefaultResourceMapper(new API(context.getManageClient()));
		}
		return resourceMapper.readResource(payload, resourceClassType).getJson();
	}

	/**
	 * Handles saving each of the given resources via the given ResourceManager. The resources may not have needed
	 * any merging, but they're still refer to as "mergedResources" to capture the fact that the merging should have
	 * happened before this method is called.
	 *
	 * @param context
	 * @param resourceManager
	 * @param mergedReferences
	 * @return
	 */
	protected List<SaveReceipt> saveMergedResources(CommandContext context, ResourceManager resourceManager,
	                                                List<ResourceReference> mergedReferences) {
		List<SaveReceipt> saveReceipts = new ArrayList<>();
		for (ResourceReference reference : mergedReferences) {
			SaveReceipt receipt = saveResource(resourceManager, context, reference.getObjectNode().toString());
			if (receipt != null) {
				saveReceipts.add(receipt);
				afterResourceSaved(resourceManager, context, reference, receipt);
			}
		}
		return saveReceipts;
	}

	/**
	 * Saves a resource that's been read from a File already.
	 *
	 * @param mgr
	 * @param context
	 * @param payload
	 * @return
	 */
	protected SaveReceipt saveResource(ResourceManager mgr, CommandContext context, String payload) {
		mgr = adjustResourceManagerForPayload(mgr, context, payload);

		// A subclass may decide that the resource shouldn't be saved by returning a null payload
		if (payload == null) {
			return null;
		}

		SaveReceipt receipt = mgr.save(payload);
		if (storeResourceIdsAsCustomTokens) {
			storeTokenForResourceId(receipt, context);
		}
		return receipt;
	}

	/**
	 * Merges the resources in the given list (if any need merging). Constructs a BiPredicate to determine which
	 * resources should be merged together.
	 *
	 * @param resources
	 * @return
	 */
	protected List<ResourceReference> mergeResources(List<ResourceReference> resources) {
		if (logger.isInfoEnabled()) {
			logger.info("Merging payloads that reference the same resource");
		}

		BiPredicate<ResourceReference, ResourceReference> biPredicate;
		if (resourceIdPropertyName != null) {
			biPredicate = new PropertyBasedBiPredicate(resourceIdPropertyName);
		} else {
			biPredicate = getBiPredicateForMergingResources();
		}
		if (biPredicate == null) {
			throw new IllegalStateException("To merge resources, either resourceIdPropertyName must be set or " +
				"getBiPredicateForMergingResources must return a BiPredicate");
		}

		return JsonNodeUtil.mergeObjectNodeList(resources, biPredicate);
	}

	/**
	 * If a subclass wants resources to be merged, and it doesn't define resourceIdPropertyName, then it must override
	 * this method to return a BiPredicate that defines whether two resources should be merged together.
	 *
	 * @return
	 */
	protected BiPredicate<ResourceReference, ResourceReference> getBiPredicateForMergingResources() {
		return null;
	}

	/**
	 * Handles reading the contents of a resource file into a String and adjusting it via
	 * adjustPayloadBeforeSavingResource. This is in a separate method for subclasses to use that needs to read in the
	 * contents of a file but don't wish to use saveResource.
	 *
	 * @param context
	 * @param f
	 * @return
	 */
	protected String readResourceFromFile(CommandContext context, File f) {
		String payload = copyFileToString(f, context);
		return adjustPayloadBeforeSavingResource(context, f, payload);
	}

	/**
	 * Subclasses can override this to add functionality after a resource has been saved.
	 * <p>
	 * Starting in version 3.0 of ml-app-deployer, this will always check if the Location header is
	 * /admin/v1/timestamp, and if so, it will wait for ML to restart.
	 *
	 * @param mgr
	 * @param context
	 * @param resourceReference
	 * @param receipt
	 */
	protected void afterResourceSaved(ResourceManager mgr, CommandContext context, ResourceReference resourceReference, SaveReceipt receipt) {
		if (receipt == null) {
			return;
		}
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

	/**
	 * Allow subclass to override this in order to fiddle with the payload before it's saved; called by saveResource.
	 * <p>
	 * A subclass can return null from this method to indicate that the resource should not be saved.
	 *
	 * @param context
	 * @param f
	 * @param payload
	 * @return
	 */
	protected String adjustPayloadBeforeSavingResource(CommandContext context, File f, String payload) {
		String[] props = context.getAppConfig().getExcludeProperties();
		if (props != null && props.length > 0) {
			logger.info(format("Excluding properties %s from payload", Arrays.asList(props).toString()));
			payload = payloadParser.excludeProperties(payload, props);
		}

		props = context.getAppConfig().getIncludeProperties();
		if (props != null && props.length > 0) {
			logger.info(format("Including only properties %s from payload", Arrays.asList(props).toString()));
			payload = payloadParser.includeProperties(payload, props);
		}

		return payload;
	}

	/**
	 * A subclass can override this when the ResourceManager needs to be adjusted based on data in the payload.
	 *
	 * @param mgr
	 * @param payload
	 * @return
	 */
	protected ResourceManager adjustResourceManagerForPayload(ResourceManager mgr, CommandContext context, String payload) {
		return mgr;
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
		URI location = receipt.getResponse() != null ? receipt.getResponse().getHeaders().getLocation() : null;

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

	protected File[] listFilesInDirectory(File dir) {
		File[] files = dir.listFiles(resourceFilenameFilter);
		if (files != null && files.length > 1) {
			Arrays.sort(files);
		}
		return files;
	}

	protected void logResourceDirectoryNotFound(File dir) {
		if (dir != null && logger.isInfoEnabled()) {
			logger.info("No resource directory found at: " + dir.getAbsolutePath());
		}
	}

	/**
	 * @param context
	 * @return true if the ML server has the CMA endpoint - /manage/v3
	 */
	protected boolean cmaEndpointExists(CommandContext context) {
		return new ConfigurationManager(context.getManageClient()).endpointExists();
	}

	/**
	 * Subclasses may override this to defer submission of a configuration so that it can be combined with other
	 * configurations.
	 *
	 * @param context
	 * @param config
	 */
	protected void deployConfiguration(CommandContext context, Configuration config) {
		if (config.hasResources()) {
			new Configurations(config).submit(context.getManageClient());
		}
	}

	protected void setIncrementalMode(boolean incrementalMode) {
		if (resourceFilenameFilter instanceof IncrementalFilenameFilter) {
			((IncrementalFilenameFilter) resourceFilenameFilter).setIncrementalMode(incrementalMode);
		} else {
			logger.warn("resourceFilenameFilter does not implement " + IncrementalFilenameFilter.class.getName() + ", and thus " +
				"setIncrementalMode cannot be invoked");
		}
	}

	/**
	 * By default, the name of a database resource directory is assumed to be the name of the database that the resources
	 * within the directory should be associated with. But starting in 3.16.0, if the name of the directory doesn't
	 * match that of an existing database, then a check is made to see if there's a database file in the given ConfigDir
	 * that has the same name, minus its extension, as the database directory name. If so, then the database-name is
	 * extracted from that file and used as the database name. If not, a warning is logged and null is returned.
	 * Previously, an exception was thrown if the database-name could not be determined, but this raised problems for
	 * users that had directory names like ".svn" that they could not easily remove (and we can't eagerly ignore certain
	 * names since something like ".svn" is a valid ML database name).
	 *
	 * @param context
	 * @param configDir
	 * @param databaseResourceDir
	 * @return
	 */
	protected String determineDatabaseNameForDatabaseResourceDirectory(CommandContext context, ConfigDir configDir, File databaseResourceDir) {
		final String dirName = databaseResourceDir.getName();

		if (new DatabaseManager(context.getManageClient()).exists(dirName)) {
			return dirName;
		}

		File databasesDir = configDir.getDatabasesDir();
		for (File f : listFilesInDirectory(databasesDir)) {
			String name = f.getName();
			int index = name.lastIndexOf('.');
			name = index > 0 ? name.substring(0, index) : name;
			if (dirName.equals(name)) {
				logger.info("Found database file with same name, minus its extension, as the database resource directory; " +
					"file: " + f);
				String payload = copyFileToString(f, context);
				String databaseName = payloadParser.getPayloadFieldValue(payload, "database-name");
				logger.info("Associating database resource directory with database: " + databaseName);
				return databaseName;
			}
		}

		logger.warn("Could not determine database to associate with database resource directory: " +
			databaseResourceDir + "; will not process any resource files in that directory");

		return null;
	}

	public void setPayloadTokenReplacer(PayloadTokenReplacer payloadTokenReplacer) {
		this.payloadTokenReplacer = payloadTokenReplacer;
	}

	public void setExecuteSortOrder(int executeSortOrder) {
		this.executeSortOrder = executeSortOrder;
	}

	public void setStoreResourceIdsAsCustomTokens(boolean storeResourceIdsAsCustomTokens) {
		this.storeResourceIdsAsCustomTokens = storeResourceIdsAsCustomTokens;
	}

	public void setResourceFilenameFilter(FilenameFilter resourceFilenameFilter) {
		this.resourceFilenameFilter = resourceFilenameFilter;
	}

	public FilenameFilter getResourceFilenameFilter() {
		return resourceFilenameFilter;
	}

	public boolean isStoreResourceIdsAsCustomTokens() {
		return storeResourceIdsAsCustomTokens;
	}

	public Class<? extends Resource> getResourceClassType() {
		return resourceClassType;
	}

	public void setResourceClassType(Class<? extends Resource> resourceClassType) {
		this.resourceClassType = resourceClassType;
	}

	public String getResourceIdPropertyName() {
		return resourceIdPropertyName;
	}

	public void setResourceIdPropertyName(String resourceIdPropertyName) {
		this.resourceIdPropertyName = resourceIdPropertyName;
	}

	public boolean isSupportsResourceMerging() {
		return supportsResourceMerging;
	}

	public void setSupportsResourceMerging(boolean supportsResourceMerging) {
		this.supportsResourceMerging = supportsResourceMerging;
	}

	protected PayloadParser getPayloadParser() {
		return payloadParser;
	}
}
