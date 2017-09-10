package com.marklogic.client.ext.modulesloader.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.admin.*;
import com.marklogic.client.admin.ResourceExtensionsManager.MethodParameters;
import com.marklogic.client.admin.ServerConfigurationManager.UpdatePolicy;
import com.marklogic.client.ext.file.DocumentFile;
import com.marklogic.client.ext.helper.FilenameUtil;
import com.marklogic.client.ext.helper.LoggingObject;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.ext.modulesloader.*;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ExecutorConfigurationSupport;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

	// For parallelizing writes of modules
	private TaskExecutor taskExecutor;
	private int taskThreadCount = 16;
	private boolean shutdownTaskExecutorAfterLoadingModules = true;

	/**
	 * When set to true, exceptions thrown while loading transforms and resources will be caught and logged, and the
	 * module will be updated as having been loaded. This is useful when running a program that watches modules for changes, as it
	 * prevents the program from crashing and also from trying to load the module over and over.
	 */
	private boolean catchExceptions = false;

	/**
	 * Use this when you don't need to load asset modules.
	 */
	public DefaultModulesLoader() {
		this.extensionMetadataProvider = new DefaultExtensionMetadataProvider();
		this.modulesManager = new PropertiesModuleManager();
	}

	public DefaultModulesLoader(AssetFileLoader assetFileLoader) {
		this();
		this.assetFileLoader = assetFileLoader;
	}

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
	public Set<File> loadModules(File baseDir, ModulesFinder modulesFinder, DatabaseClient client) {
		if (logger.isDebugEnabled()) {
			logger.debug("Loading modules from base directory: " + baseDir.getAbsolutePath());
		}
		setDatabaseClient(client);

		if (modulesManager != null) {
			modulesManager.initialize();
		}

		Modules modules = modulesFinder.findModules(baseDir);

		if (taskExecutor == null) {
			initializeDefaultTaskExecutor();
		}

		Set<File> loadedModules = new HashSet<>();
		loadProperties(modules, loadedModules);
		loadNamespaces(modules, loadedModules);
		loadAssets(modules, loadedModules);

		loadQueryOptions(modules, loadedModules);
		loadTransforms(modules, loadedModules);
		loadResources(modules, loadedModules);

		waitForTaskExecutorToFinish();

		if (logger.isDebugEnabled()) {
			logger.debug("Finished loading modules from base directory: " + baseDir.getAbsolutePath());
		}
		return loadedModules;
	}

	/**
	 * If an AsyncTaskExecutor is used for loading options/services/transforms, we need to wait for the tasks to complete
	 * before we e.g. release the DatabaseClient.
	 */
	protected void waitForTaskExecutorToFinish() {
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
	 * Specialized method for loading modules from the classpath. Currently does not support loading asset modules.
	 *
	 * @param rootPath
	 * @param client
	 */
	public void loadClasspathModules(String rootPath, DatabaseClient client) {
		setDatabaseClient(client);

		Modules modules = new DefaultModulesFinder().findClasspathModules("classpath:" + rootPath);

		ExtensionLibrariesManager mgr = client.newServerConfigManager().newExtensionLibrariesManager();
		for (Resource r : modules.getAssets()) {
			installAsset(r, rootPath, mgr);
		}

		for (Resource r : modules.getNamespaces()) {
			installNamespace(r);
		}

		for (Resource r : modules.getOptions()) {
			installQueryOptions(r);
		}

		for (Resource r : modules.getTransforms()) {
			installTransform(r, new ExtensionMetadata());
		}

		for (Resource r : modules.getServices()) {
			installService(r, new ExtensionMetadata());
		}

		waitForTaskExecutorToFinish();
	}

	/**
	 * This method is useful for when loading assets from a resource from the classpath. For loading modules from a
	 * filesystem, just use installAssets, which uses the much more powerful/flexible XccAssetLoader.
	 *
	 * @param r
	 * @param rootPath
	 * @param mgr
	 */
	public void installAsset(Resource r, String rootPath, ExtensionLibrariesManager mgr) {
		try {
			String path = r.getURL().getPath();
			if (logger.isDebugEnabled()) {
				logger.debug("Original asset URL path: " + path);
			}
			if (path.contains("!")) {
				path = path.split("!")[1];
				if (logger.isDebugEnabled()) {
					logger.debug("Path after ! symbol: " + path);
				}
				if (path.startsWith(rootPath)) {
					path = path.substring(rootPath.length());
					if (logger.isDebugEnabled()) {
						logger.debug("Path without root path: " + path);
					}
				}
			}
			if (logger.isInfoEnabled()) {
				logger.info("Writing asset at path: " + path);
			}
			mgr.write(path, new InputStreamHandle(r.getInputStream()));
		} catch (IOException ie) {
			logger.error("Unable to load asset from resource: " + r.getFilename() + "; cause: " + ie.getMessage(), ie);
			logger.error("Will continue trying to load other modules");
		}
	}

	/**
	 * Only supports a JSON file.
	 *
	 * @param modules
	 * @param loadedModules
	 */
	protected void loadProperties(Modules modules, Set<File> loadedModules) {
		Resource r = modules.getPropertiesFile();
		if (r != null && r.exists()) {
			File f = getFileFromResource(r);
			if (modulesManager != null && !modulesManager.hasFileBeenModifiedSinceLastLoaded(f)) {
				return;
			}

			ServerConfigurationManager mgr = client.newServerConfigManager();
			ObjectMapper m = new ObjectMapper();
			try {
				JsonNode node = m.readTree(f);
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
			} catch (Exception e) {
				throw new RuntimeException("Unable to read REST configuration from file: " + f.getAbsolutePath(), e);
			}

			if (modulesManager != null) {
				modulesManager.saveLastLoadedTimestamp(f, new Date());
			}

			loadedModules.add(f);
		}
	}

	protected File getFileFromResource(Resource r) {
		try {
			return r.getFile();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	protected void loadAssets(Modules modules, Set<File> loadedModules) {
		List<Resource> dirs = modules.getAssetDirectories();
		if (dirs == null || dirs.isEmpty()) {
			return;
		}

		String[] paths = new String[dirs.size()];
		for (int i = 0; i < dirs.size(); i++) {
			paths[i] = getFileFromResource(dirs.get(i)).getAbsolutePath();
		}

		Set<File> files = null;

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
		files = new HashSet<>();
		for (DocumentFile asset : list) {
			files.add(asset.getFile());
		}

		if (files != null) {
			loadedModules.addAll(files);
		}
	}

	protected void loadQueryOptions(Modules modules, Set<File> loadedModules) {
		if (modules.getOptions() == null) {
			return;
		}

		for (Resource r : modules.getOptions()) {
			File f = installQueryOptions(getFileFromResource(r));
			if (f != null) {
				loadedModules.add(f);
			}
		}
	}

	protected void loadTransforms(Modules modules, Set<File> loadedModules) {
		if (modules.getTransforms() == null) {
			return;
		}

		for (Resource r : modules.getTransforms()) {
			File f = getFileFromResource(r);
			try {
				ExtensionMetadataAndParams emap = extensionMetadataProvider.provideExtensionMetadataAndParams(r);
				f = installTransform(f, emap.metadata);
				if (f != null) {
					loadedModules.add(f);
				}
			} catch (RuntimeException e) {
				if (catchExceptions) {
					logger.warn("Unable to load module from file: " + f.getAbsolutePath() + "; cause: " + e.getMessage(), e);
					loadedModules.add(f);
					if (modulesManager != null) {
						modulesManager.saveLastLoadedTimestamp(f, new Date());
					}
				} else {
					throw e;
				}
			}
		}
	}

	protected void loadResources(Modules modules, Set<File> loadedModules) {
		if (modules.getServices() == null) {
			return;
		}

		for (Resource r : modules.getServices()) {
			File f = getFileFromResource(r);
			try {
				ExtensionMetadataAndParams emap = extensionMetadataProvider.provideExtensionMetadataAndParams(r);
				f = installService(f, emap.metadata, emap.methods.toArray(new MethodParameters[]{}));
			} catch (RuntimeException e) {
				if (catchExceptions) {
					logger.warn("Unable to load module from file: " + f.getAbsolutePath() + "; cause: " + e.getMessage(), e);
					loadedModules.add(f);
					if (modulesManager != null) {
						modulesManager.saveLastLoadedTimestamp(f, new Date());
					}
				} else {
					throw e;
				}
			}
			if (f != null) {
				loadedModules.add(f);
			}
		}
	}

	protected void loadNamespaces(Modules modules, Set<File> loadedModules) {
		if (modules.getNamespaces() == null) {
			return;
		}

		for (Resource r : modules.getNamespaces()) {
			File f = getFileFromResource(r);
			f = installNamespace(f);
			if (f != null) {
				loadedModules.add(f);
			}
		}
	}

	public File installService(File file, ExtensionMetadata metadata, MethodParameters... methodParams) {
		if (modulesManager != null && !modulesManager.hasFileBeenModifiedSinceLastLoaded(file)) {
			return null;
		}
		installService(new FileSystemResource(file), metadata, methodParams);
		if (modulesManager != null) {
			modulesManager.saveLastLoadedTimestamp(file, new Date());
		}
		return file;
	}

	public void installService(Resource r, final ExtensionMetadata metadata, final MethodParameters... methodParams) {
		final ResourceExtensionsManager extMgr = client.newServerConfigManager().newResourceExtensionsManager();
		final String resourceName = getExtensionNameFromFile(r);
		if (metadata.getTitle() == null) {
			metadata.setTitle(resourceName + " resource extension");
		}
		logger.info(String.format("Loading %s resource extension from file %s", resourceName, r.getFilename()));
		InputStreamHandle h;
		try {
			h = new InputStreamHandle(r.getInputStream());
		} catch (IOException ie) {
			throw new RuntimeException("Unable to read service resource: " + ie.getMessage(), ie);
		}
		final InputStreamHandle finalHandle = h;
		executeTask(new Runnable() {
			@Override
			public void run() {
				extMgr.writeServices(resourceName, finalHandle, metadata, methodParams);
			}
		});
	}

	public File installTransform(File file, ExtensionMetadata metadata) {
		if (modulesManager != null && !modulesManager.hasFileBeenModifiedSinceLastLoaded(file)) {
			return null;
		}
		installTransform(new FileSystemResource(file), metadata);
		if (modulesManager != null) {
			modulesManager.saveLastLoadedTimestamp(file, new Date());
		}
		return file;
	}

	public void installTransform(Resource r, final ExtensionMetadata metadata) {
		final String filename = r.getFilename();
		final TransformExtensionsManager mgr = client.newServerConfigManager().newTransformExtensionsManager();
		final String transformName = getExtensionNameFromFile(r);
		logger.info(String.format("Loading %s transform from resource %s", transformName, filename));
		InputStreamHandle h = null;
		try {
			h = new InputStreamHandle(r.getInputStream());
		} catch (IOException ie) {
			throw new RuntimeException("Unable to read transform resource: " + ie.getMessage(), ie);
		}
		final InputStreamHandle finalHandle = h;
		executeTask(new Runnable() {
			@Override
			public void run() {
				if (FilenameUtil.isXslFile(filename)) {
					mgr.writeXSLTransform(transformName, finalHandle, metadata);
				} else if (FilenameUtil.isJavascriptFile(filename)) {
					mgr.writeJavascriptTransform(transformName, finalHandle, metadata);
				} else {
					mgr.writeXQueryTransform(transformName, finalHandle, metadata);
				}
			}
		});
	}

	public File installQueryOptions(File f) {
		if (modulesManager != null && !modulesManager.hasFileBeenModifiedSinceLastLoaded(f)) {
			return null;
		}
		installQueryOptions(new FileSystemResource(f));
		if (modulesManager != null) {
			modulesManager.saveLastLoadedTimestamp(f, new Date());
		}
		return f;
	}

	public void installQueryOptions(Resource r) {
		final String filename = r.getFilename();
		final String name = getExtensionNameFromFile(r);
		logger.info(String.format("Loading %s query options from file %s", name, filename));
		final QueryOptionsManager mgr = client.newServerConfigManager().newQueryOptionsManager();
		InputStreamHandle h;
		try {
			h = new InputStreamHandle(r.getInputStream());
		} catch (IOException ie) {
			throw new RuntimeException("Unable to read transform resource: " + ie.getMessage(), ie);
		}
		final InputStreamHandle writeHandle = h;
		executeTask(new Runnable() {
			@Override
			public void run() {
				if (filename.endsWith(".json")) {
					mgr.writeOptions(name, writeHandle.withFormat(Format.JSON));
				} else {
					mgr.writeOptions(name, writeHandle);
				}
			}
		});
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
		taskExecutor.execute(r);
	}

	public File installNamespace(File f) {
		if (modulesManager != null && !modulesManager.hasFileBeenModifiedSinceLastLoaded(f)) {
			return null;
		}
		installNamespace(new FileSystemResource(f));
		if (modulesManager != null) {
			modulesManager.saveLastLoadedTimestamp(f, new Date());
		}
		return f;
	}

	public void installNamespace(Resource r) {
		String prefix = getExtensionNameFromFile(r);
		String namespaceUri = null;
		try {
			namespaceUri = new String(FileCopyUtils.copyToByteArray(r.getInputStream()));
		} catch (IOException ie) {
			logger.error("Unable to install namespace from file: " + r.getFilename(), ie);
			return;
		}
		NamespacesManager mgr = client.newServerConfigManager().newNamespacesManager();
		String existingUri = mgr.readPrefix(prefix);
		if (existingUri != null) {
			logger.info(String.format("Deleting namespace with prefix of %s and URI of %s", prefix, existingUri));
			mgr.deletePrefix(prefix);
		}
		logger.info(String.format("Adding namespace with prefix of %s and URI of %s", prefix, namespaceUri));
		mgr.addPrefix(prefix, namespaceUri);
	}

	protected String getExtensionNameFromFile(Resource r) {
		String name = r.getFilename();
		int pos = name.lastIndexOf('.');
		if (pos < 0)
			return name;
		return name.substring(0, pos);
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
}
