package com.marklogic.client.ext.modulesloader.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.admin.*;
import com.marklogic.client.admin.ResourceExtensionsManager.MethodParameters;
import com.marklogic.client.admin.ServerConfigurationManager.UpdatePolicy;
import com.marklogic.client.ext.file.DefaultDocumentFileReader;
import com.marklogic.client.ext.file.DocumentFile;
import com.marklogic.client.ext.file.DocumentFileReader;
import com.marklogic.client.ext.helper.FilenameUtil;
import com.marklogic.client.ext.helper.LoggingObject;
import com.marklogic.client.ext.modulesloader.*;
import com.marklogic.client.ext.tokenreplacer.TokenReplacer;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.core.io.Resource;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ExecutorConfigurationSupport;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Default implementation of ModulesLoader. Loads everything except assets via the REST API. Assets are either loaded
 * via an XccAssetLoader (faster) or via a RestApiAssetLoader (slower, but doesn't require additional privileges).
 */
public class DefaultModulesLoader extends LoggingObject implements ModulesLoader {

	private DatabaseClient client;
	private AssetFileLoader assetFileLoader;
	private ExtensionMetadataProvider extensionMetadataProvider;
	private ModulesManager modulesManager;
	private StaticChecker staticChecker;

	// For parallelizing writes of REST API modules - e.g. services/options/transforms
	private TaskExecutor taskExecutor;
	private int taskThreadCount = 8;
	private boolean shutdownTaskExecutorAfterLoadingModules = true;

	// For replacing tokens in options/services/transforms
	// Tokens in asset modules are replaced via the AssetFileLoader instance
	private TokenReplacer tokenReplacer;

	private List<LoadModulesFailureListener> failureListeners = new ArrayList<>();

	/**
	 * When set to true, exceptions thrown while loading transforms and resources will be caught and logged, and the
	 * module will be updated as having been loaded. This is useful when running a program that watches modules for changes, as it
	 * prevents the program from crashing and also from trying to load the module over and over.
	 */
	private boolean catchExceptions = false;

	/**
	 * When not null, this will be applied against every Resource that's found and if it doesn't match the pattern,
	 * it will not be loaded.
	 */
	private Pattern includeFilenamePattern;

	/**
	 * Use this when you need to load REST modules and asset modules as well (non-REST modules).
	 *
	 * @param assetFileLoader
	 */
	public DefaultModulesLoader(AssetFileLoader assetFileLoader) {
		this();
		this.assetFileLoader = assetFileLoader;
	}

	/**
	 * Use this when you don't need to load asset modules.
	 */
	public DefaultModulesLoader() {
		this.extensionMetadataProvider = new DefaultExtensionMetadataProvider();
		this.modulesManager = new PropertiesModuleManager();
		failureListeners.add(new SimpleLoadModulesFailureListener());
	}

	/**
	 *
	 */
	public void initializeDefaultTaskExecutor() {
		if (taskThreadCount > 1) {
			ThreadPoolTaskExecutor tpte = new ThreadPoolTaskExecutor();
			tpte.setCorePoolSize(taskThreadCount);

			// 10 minutes should be plenty of time to wait for REST API modules to be loaded
			tpte.setAwaitTerminationSeconds(60 * 10);
			tpte.setWaitForTasksToCompleteOnShutdown(true);

			tpte.afterPropertiesSet();
			this.taskExecutor = tpte;
		} else {
			this.taskExecutor = new SyncTaskExecutor();
		}
	}

	/**
	 * Load modules from the given base directory, selecting modules via the given ModulesFinder, and loading them via
	 * the given DatabaseClient. Note that asset modules will not be loaded by the DatabaseClient that's passed in here,
	 * because the /v1/ext endpoint is so slow - load assets instead via a RestApiAssetLoader or an XccAssetLoader
	 * passed into a constructor for this class.
	 */
	public Set<Resource> loadModules(String baseDir, ModulesFinder modulesFinder, DatabaseClient client) {
		if (logger.isDebugEnabled()) {
			logger.debug("Loading modules from base directory: " + baseDir);
		}
		setDatabaseClient(client);

		if (modulesManager != null) {
			modulesManager.initialize();
		}

		Modules modules = modulesFinder.findModules(baseDir);

		if (taskExecutor == null) {
			initializeDefaultTaskExecutor();
		}

		Set<Resource> loadedModules = new HashSet<>();
		loadProperties(modules, loadedModules);
		loadNamespaces(modules, loadedModules);
		loadAssets(modules, loadedModules);

		loadQueryOptions(modules, loadedModules);
		loadTransforms(modules, loadedModules);
		loadResources(modules, loadedModules);

		waitForTaskExecutorToFinish();

		if (logger.isDebugEnabled()) {
			logger.debug("Finished loading modules from base directory: " + baseDir);
		}

		return loadedModules;
	}

	/**
	 * If an AsyncTaskExecutor is used for loading options/services/transforms, we need to wait for the tasks to complete
	 * before we e.g. release the DatabaseClient.
	 */
	public void waitForTaskExecutorToFinish() {
		if (shutdownTaskExecutorAfterLoadingModules) {
			if (taskExecutor instanceof ExecutorConfigurationSupport) {
				((ExecutorConfigurationSupport) taskExecutor).shutdown();
				taskExecutor = null;
			} else if (taskExecutor instanceof DisposableBean) {
				try {
					((DisposableBean) taskExecutor).destroy();
				} catch (Exception ex) {
					logger.warn("Unexpected exception while calling destroy() on taskExecutor: " + ex.getMessage(), ex);
				}
				taskExecutor = null;
			}
		} else if (logger.isDebugEnabled()) {
			logger.debug("shutdownTaskExecutorAfterLoadingModules is set to false, so not shutting down taskExecutor");
		}
	}

	/**
	 * Only supports a JSON file.
	 *
	 * @param modules
	 * @param loadedModules
	 */
	protected void loadProperties(Modules modules, Set<Resource> loadedModules) {
		Resource r = modules.getPropertiesFile();
		if (r == null || !r.exists() || ignoreResource(r)) {
			return;
		}
		File f = getFileFromResource(r);
		if (f != null && modulesManager != null && !modulesManager.hasFileBeenModifiedSinceLastLoaded(f)) {
			return;
		}

		ServerConfigurationManager mgr = client.newServerConfigManager();
		ObjectMapper m = new ObjectMapper();
		JsonNode node = null;
		try {
			node = m.readTree(r.getInputStream());
		} catch (IOException ex) {
			throw new RuntimeException("Unable to read REST configuration from file: " + f.getAbsolutePath(), ex);
		}
		if (node.has("document-transform-all")) {
			mgr.setDefaultDocumentReadTransformAll(node.get("document-transform-all").asBoolean());
		}
		if (node.has("document-transform-out")) {
			mgr.setDefaultDocumentReadTransform(node.get("document-transform-out").asText());
		}
		if (node.has("update-policy")) {
			mgr.setUpdatePolicy(UpdatePolicy.valueOf(node.get("update-policy").asText()));
		}
		if (node.has("validate-options")) {
			mgr.setQueryOptionValidation(node.get("validate-options").asBoolean());
		}
		if (node.has("validate-queries")) {
			mgr.setQueryValidation(node.get("validate-queries").asBoolean());
		}
		if (node.has("debug")) {
			mgr.setServerRequestLogging(node.get("debug").asBoolean());
		}
		if (logger.isInfoEnabled()) {
			logger.info("Writing REST server configuration");
			logger.info("Default document read transform: " + mgr.getDefaultDocumentReadTransform());
			logger.info("Transform all documents on read: " + mgr.getDefaultDocumentReadTransformAll());
			logger.info("Validate query options: " + mgr.getQueryOptionValidation());
			logger.info("Validate queries: " + mgr.getQueryValidation());
			logger.info("Output debugging: " + mgr.getServerRequestLogging());
			if (mgr.getUpdatePolicy() != null) {
				logger.info("Update policy: " + mgr.getUpdatePolicy().name());
			}
		}
		mgr.writeConfiguration();

		if (f != null && modulesManager != null) {
			modulesManager.saveLastLoadedTimestamp(f, new Date());
		}

		loadedModules.add(r);
	}

	protected File getFileFromResource(Resource r) {
		try {
			return r.getFile();
		} catch (IOException ex) {}
		return null;
	}

	/**
	 *
	 * @param modules
	 * @param loadedModules
	 */
	protected void loadAssets(Modules modules, Set<Resource> loadedModules) {
		List<Resource> dirs = modules.getAssetDirectories();
		if (dirs == null || dirs.isEmpty()) {
			return;
		}

		String[] paths = new String[dirs.size()];
		for (int i = 0; i < dirs.size(); i++) {
			try {
				paths[i] = dirs.get(i).getURI().toString();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Loading asset modules from paths: " + Arrays.asList(paths));
		}

		if (includeFilenamePattern != null) {
			// Make sure the DocumentFileReader is not null
			assetFileLoader.initializeDocumentFileReader();
			DocumentFileReader dfr = assetFileLoader.getDocumentFileReader();
			if (dfr instanceof DefaultDocumentFileReader) {
				DefaultDocumentFileReader reader = (DefaultDocumentFileReader)dfr;
				reader.addDocumentFileProcessor(documentFile -> {
					File f = documentFile.getFile();
					if (f == null) {
						return null;
					}
					if (!includeFilenamePattern.matcher(f.getAbsolutePath()).matches()) {
						return null;
					}
					return documentFile;
				});
			}
		}

		List<DocumentFile> list = assetFileLoader.loadFiles(paths);
		if (staticChecker != null && !list.isEmpty()) {
			try {
				staticChecker.checkLoadedAssets(list);
			} catch (RuntimeException ex) {
				if (catchExceptions) {
					logger.error("Static check failure: " + ex.getMessage());
				} else {
					throw ex;
				}
			}
		}

		for (DocumentFile asset : list) {
			loadedModules.add(asset.getResource());
		}
	}

	/**
	 *
	 * @param modules
	 * @param loadedModules
	 */
	protected void loadQueryOptions(Modules modules, Set<Resource> loadedModules) {
		if (modules.getOptions() == null) {
			return;
		}

		for (Resource r : modules.getOptions()) {
			if (installQueryOptions(r) != null) {
				loadedModules.add(r);
			}
		}
	}

	/**
	 *
	 * @param modules
	 * @param loadedModules
	 */
	protected void loadTransforms(Modules modules, Set<Resource> loadedModules) {
		if (modules.getTransforms() == null) {
			return;
		}

		for (Resource r : modules.getTransforms()) {
			try {
				ExtensionMetadataAndParams emap = extensionMetadataProvider.provideExtensionMetadataAndParams(r);
				if (installTransform(r, emap.metadata) != null) {
					loadedModules.add(r);
				}
			} catch (RuntimeException e) {
				if (catchExceptions) {
					logger.warn("Unable to load module from file: " + r.getFilename() + "; cause: " + e.getMessage(), e);
					// update the timestamp so that mlWatch doesn't keep trying again and again
					loadedModules.add(r);
					updateTimestamp(r);
				} else {
					throw e;
				}
			}
		}
	}

	/**
	 *
	 * @param modules
	 * @param loadedModules
	 */
	protected void loadResources(Modules modules, Set<Resource> loadedModules) {
		if (modules.getServices() == null) {
			return;
		}

		for (Resource r : modules.getServices()) {
			try {
				ExtensionMetadataAndParams emap = extensionMetadataProvider.provideExtensionMetadataAndParams(r);
				if (installService(r, emap.metadata, emap.methods.toArray(new MethodParameters[]{})) != null) {
					loadedModules.add(r);
				}
			} catch (RuntimeException e) {
				if (catchExceptions) {
					logger.warn("Unable to load module from file: " + r.getFilename() + "; cause: " + e.getMessage(), e);
					// update the timestamp so that mlWatch doesn't keep trying again and again
					loadedModules.add(r);
					updateTimestamp(r);
				} else {
					throw e;
				}
			}
		}
	}

	/**
	 *
	 * @param modules
	 * @param loadedModules
	 */
	protected void loadNamespaces(Modules modules, Set<Resource> loadedModules) {
		if (modules.getNamespaces() == null) {
			return;
		}

		for (Resource r : modules.getNamespaces()) {
			if (installNamespace(r) != null) {
				loadedModules.add(r);
			}
		}
	}

	/**
	 *
	 * @param r
	 * @param metadata
	 * @param methodParams
	 * @return
	 */
	public Resource installService(Resource r, final ExtensionMetadata metadata, final MethodParameters... methodParams) {
		if (!hasFileBeenModified(r) || ignoreResource(r)) {
			return null;
		}
		final ResourceExtensionsManager extMgr = client.newServerConfigManager().newResourceExtensionsManager();
		final String resourceName = getExtensionNameFromFile(r);
		if (metadata.getTitle() == null) {
			metadata.setTitle(resourceName + " resource extension");
		}
		logger.info(String.format("Loading %s resource extension from file %s", resourceName, r.getFilename()));

		StringHandle h = new StringHandle(readAndReplaceTokens(r));
		executeTask(() -> extMgr.writeServices(resourceName, h, metadata, methodParams));

		updateTimestamp(r);
		return r;
	}

	/**
	 *
	 * @param r
	 * @param metadata
	 * @return
	 */
	public Resource installTransform(Resource r, final ExtensionMetadata metadata) {
		if (!hasFileBeenModified(r) || ignoreResource(r)) {
			return null;
		}
		final String filename = r.getFilename();
		final TransformExtensionsManager mgr = client.newServerConfigManager().newTransformExtensionsManager();
		final String transformName = getExtensionNameFromFile(r);
		logger.info(String.format("Loading %s transform from resource %s", transformName, filename));

		StringHandle h = new StringHandle(readAndReplaceTokens(r));
		executeTask(() -> {
            if (FilenameUtil.isXslFile(filename)) {
                mgr.writeXSLTransform(transformName, h, metadata);
            } else if (FilenameUtil.isJavascriptFile(filename)) {
                mgr.writeJavascriptTransform(transformName, h, metadata);
            } else {
                mgr.writeXQueryTransform(transformName, h, metadata);
            }
        });
		updateTimestamp(r);

		return r;
	}

	/**
	 *
	 * @param r
	 * @return
	 */
	public Resource installQueryOptions(Resource r) {
		if (!hasFileBeenModified(r) || ignoreResource(r)) {
			return null;
		}

		final String filename = r.getFilename();
		final String name = getExtensionNameFromFile(r);
		logger.info(String.format("Loading %s query options from file %s", name, filename));
		final QueryOptionsManager mgr = client.newServerConfigManager().newQueryOptionsManager();

		StringHandle h = new StringHandle(readAndReplaceTokens(r));
		executeTask(() -> {
            if (filename.endsWith(".json")) {
                mgr.writeOptions(name, h.withFormat(Format.JSON));
            } else {
                mgr.writeOptions(name, h);
            }
        });
		updateTimestamp(r);
		return r;
	}

	/**
	 * Handles reading in the content of a Resource, and then replacing tokens in it if a tokenReplacer has been set.
	 */
	protected String readAndReplaceTokens(Resource r) {
		String content;
		try {
			content = new String(FileCopyUtils.copyToByteArray(r.getInputStream()));
		} catch (IOException e) {
			throw new RuntimeException("Unable to read content from: " + r.getDescription() + "; cause: " + e.getMessage(), e);
		}

		if (tokenReplacer != null) {
			content = tokenReplacer.replaceTokens(content);
		}

		return content;
	}

	/**
	 * Protected in case a subclass wants to execute the Runnable in a different way - e.g. capturing the Future
	 * that could be returned.
	 *
	 * @param r
	 */
	protected void executeTask(Runnable r) {
		if (taskExecutor == null) {
			initializeDefaultTaskExecutor();
		}
		taskExecutor.execute(() -> {
			try {
				r.run();
			}
			catch(Exception e) {
				failureListeners.forEach(listener -> listener.processFailure(e));
			}
		});
	}

	/**
	 *
	 * @param r
	 * @return
	 */
	public Resource installNamespace(Resource r) {
		if (!hasFileBeenModified(r) || ignoreResource(r)) {
			return null;
		}

		String prefix = getExtensionNameFromFile(r);
		String namespaceUri;
		try {
			namespaceUri = new String(FileCopyUtils.copyToByteArray(r.getInputStream()));
		} catch (IOException ie) {
			logger.error("Unable to install namespace from file: " + r.getFilename(), ie);
			return null;
		}
		NamespacesManager mgr = client.newServerConfigManager().newNamespacesManager();
		String existingUri = mgr.readPrefix(prefix);
		if (existingUri != null) {
			logger.info(String.format("Deleting namespace with prefix of %s and URI of %s", prefix, existingUri));
			mgr.deletePrefix(prefix);
		}
		logger.info(String.format("Adding namespace with prefix of %s and URI of %s", prefix, namespaceUri));
		mgr.addPrefix(prefix, namespaceUri);
		updateTimestamp(r);

		return r;
	}

	/**
	 *
	 * @param r
	 * @return
	 */
	protected String getExtensionNameFromFile(Resource r) {
		String name = r.getFilename();
		int pos = name.lastIndexOf('.');
		if (pos < 0)
			return name;
		return name.substring(0, pos);
	}

	/**
	 * If includeFilenamePattern is not null, then it is matched against the absolute path of the File that is resolved from
	 * the given resource. If the pattern doesn't match the absolute path, then true is returned. If a File cannot be
	 * resolved, then false is returned.
	 *
	 * @param r
	 * @return
	 */
	protected boolean ignoreResource(Resource r) {
		if (includeFilenamePattern != null) {
			File file = null;
			try {
				file = r.getFile();
			} catch (IOException e) {
				if (logger.isInfoEnabled()) {
					logger.info("Cannot resolve a File for resource: " + r + "; cannot determine if file should be ignored or not; cause: " + e.getMessage());
				}
			}
			if (file != null && !includeFilenamePattern.matcher(file.getAbsolutePath()).matches()) {
				return true;
			}
		}
		return false;
	}

	public void setDatabaseClient(DatabaseClient client) {
		this.client = client;
	}

	public void setExtensionMetadataProvider(ExtensionMetadataProvider extensionMetadataProvider) {
		this.extensionMetadataProvider = extensionMetadataProvider;
	}

	public void setModulesManager(ModulesManager modulesManager) {
		this.modulesManager = modulesManager;
	}

	public boolean isCatchExceptions() {
		return catchExceptions;
	}

	public void setCatchExceptions(boolean catchExceptions) {
		this.catchExceptions = catchExceptions;
	}

	public ExtensionMetadataProvider getExtensionMetadataProvider() {
		return extensionMetadataProvider;
	}

	public ModulesManager getModulesManager() {
		return modulesManager;
	}

	public void setStaticChecker(StaticChecker staticChecker) {
		this.staticChecker = staticChecker;
	}

	public StaticChecker getStaticChecker() {
		return staticChecker;
	}

	public void setTaskExecutor(TaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}

	public void setTaskThreadCount(int taskThreadCount) {
		this.taskThreadCount = taskThreadCount;
	}

	public void setShutdownTaskExecutorAfterLoadingModules(boolean shutdownTaskExecutorAfterLoadingModules) {
		this.shutdownTaskExecutorAfterLoadingModules = shutdownTaskExecutorAfterLoadingModules;
	}

	public void setAssetFileLoader(AssetFileLoader assetFileLoader) {
		this.assetFileLoader = assetFileLoader;
	}

	public void addFailureListener(LoadModulesFailureListener listener) {
		this.failureListeners.add(listener);
	}

	public void removeFailureListener(LoadModulesFailureListener listener) {
		this.failureListeners.remove(listener);
	}

	private boolean hasFileBeenModified(Resource resource) {
		boolean modified = true;
		if (modulesManager != null) {
			try {
				File file = resource.getFile();
				modified = modulesManager.hasFileBeenModifiedSinceLastLoaded(file);
			} catch (IOException e) {}
		}
		return modified;
	}

	private void updateTimestamp(Resource resource) {
		if (modulesManager != null) {
			try {
				File file = resource.getFile();
				modulesManager.saveLastLoadedTimestamp(file, new Date());
			} catch (IOException e) {}
		}
	}

	public AssetFileLoader getAssetFileLoader() {
		return assetFileLoader;
	}

	public List<LoadModulesFailureListener> getFailureListeners() {
		return failureListeners;
	}

	public TokenReplacer getTokenReplacer() {
		return tokenReplacer;
	}

	public void setTokenReplacer(TokenReplacer tokenReplacer) {
		this.tokenReplacer = tokenReplacer;
	}

	public void setIncludeFilenamePattern(Pattern includeFilenamePattern) {
		this.includeFilenamePattern = includeFilenamePattern;
	}
}
