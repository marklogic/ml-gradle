package com.rjrudin.marklogic.modulesloader.impl;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.util.FileCopyUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.admin.ExtensionMetadata;
import com.marklogic.client.admin.NamespacesManager;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.admin.ResourceExtensionsManager;
import com.marklogic.client.admin.ResourceExtensionsManager.MethodParameters;
import com.marklogic.client.admin.ServerConfigurationManager;
import com.marklogic.client.admin.ServerConfigurationManager.UpdatePolicy;
import com.marklogic.client.admin.TransformExtensionsManager;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.Format;
import com.rjrudin.marklogic.client.FilenameUtil;
import com.rjrudin.marklogic.client.LoggingObject;
import com.rjrudin.marklogic.modulesloader.ExtensionMetadataAndParams;
import com.rjrudin.marklogic.modulesloader.ExtensionMetadataProvider;
import com.rjrudin.marklogic.modulesloader.Modules;
import com.rjrudin.marklogic.modulesloader.ModulesFinder;
import com.rjrudin.marklogic.modulesloader.ModulesManager;

/**
 * Uses the REST API for loading all modules except "assets", which are loaded via XCC for speed reasons. Note that this
 * class will not be threadsafe since XccAssetLoader is not currently threadsafe either.
 */
public class DefaultModulesLoader extends LoggingObject implements com.rjrudin.marklogic.modulesloader.ModulesLoader {

    private DatabaseClient client;

    private XccAssetLoader xccAssetLoader;
    private ExtensionMetadataProvider extensionMetadataProvider;
    private ModulesFinder modulesFinder;
    private ModulesManager modulesManager;

    /**
     * When set to true, exceptions thrown while loading transforms and resources will be caught and logged, and the
     * module will be updated as having been loaded. This is useful when running a program like ModulesWatcher, as it
     * prevents the program from crashing and also from trying to load the module over and over.
     */
    private boolean catchExceptions = false;

    public DefaultModulesLoader(XccAssetLoader xccAssetLoader) {
        this.extensionMetadataProvider = new DefaultExtensionMetadataProvider();
        this.modulesFinder = new DefaultModulesFinder();
        this.modulesManager = new PropertiesModuleManager();
        this.xccAssetLoader = xccAssetLoader;
    }

    public Set<File> loadModules(File baseDir, DatabaseClient client) {
        setDatabaseClient(client);

        if (modulesManager != null) {
            modulesManager.initialize();
        }

        Modules modules = modulesFinder.findModules(baseDir);

        Set<File> loadedModules = new HashSet<>();

        loadProperties(modules, loadedModules);
        loadNamespaces(modules, loadedModules);
        loadAssets(modules, loadedModules);
        loadQueryOptions(modules, loadedModules);
        loadTransforms(modules, loadedModules);
        loadResources(modules, loadedModules);

        return loadedModules;
    }

    /**
     * Only supports a JSON file.
     * 
     * @param modules
     * @param loadedModules
     */
    protected void loadProperties(Modules modules, Set<File> loadedModules) {
        File f = modules.getPropertiesFile();
        if (f != null && f.exists()) {
            if (modulesManager != null && !modulesManager.hasFileBeenModifiedSinceLastInstalled(f)) {
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
                    mgr.setQueryValidation(node.get("validate-options").asBoolean());
                }
                if (node.has("validate-queries")) {
                    mgr.setQueryOptionValidation(node.get("validate-queries").asBoolean());
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
                modulesManager.saveLastInstalledTimestamp(f, new Date());
            }

            loadedModules.add(f);
        }
    }

    protected void loadAssets(Modules modules, Set<File> loadedModules) {
        List<File> dirs = modules.getAssetDirectories();
        if (dirs == null || dirs.isEmpty()) {
            return;
        }

        if (xccAssetLoader != null) {
            xccAssetLoader.setModulesManager(modulesManager);
        }

        String[] paths = new String[dirs.size()];
        for (int i = 0; i < dirs.size(); i++) {
            paths[i] = dirs.get(i).getAbsolutePath();
        }
        Set<File> files = xccAssetLoader.loadAssetsViaXcc(paths);

        if (files != null) {
            loadedModules.addAll(files);
        }
    }

    protected void loadQueryOptions(Modules modules, Set<File> loadedModules) {
        if (modules.getOptions() == null) {
            return;
        }

        for (File f : modules.getOptions()) {
            f = installQueryOptions(f);
            if (f != null) {
                loadedModules.add(f);
            }
        }
    }

    protected void loadTransforms(Modules modules, Set<File> loadedModules) {
        if (modules.getTransforms() == null) {
            return;
        }

        for (File f : modules.getTransforms()) {
            ExtensionMetadataAndParams emap = extensionMetadataProvider.provideExtensionMetadataAndParams(f);

            try {
                f = installTransform(f, emap.metadata);
                if (f != null) {
                    loadedModules.add(f);
                }
            } catch (Exception e) {
                if (catchExceptions) {
                    logger.warn(
                            "Unable to load module from file: " + f.getAbsolutePath() + "; cause: " + e.getMessage(), e);
                    loadedModules.add(f);
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

        for (File f : modules.getServices()) {
            ExtensionMetadataAndParams emap = extensionMetadataProvider.provideExtensionMetadataAndParams(f);

            try {
                f = installResource(f, emap.metadata, emap.methods.toArray(new MethodParameters[] {}));
            } catch (Exception e) {
                if (catchExceptions) {
                    logger.warn(
                            "Unable to load module from file: " + f.getAbsolutePath() + "; cause: " + e.getMessage(), e);
                    loadedModules.add(f);
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

        for (File f : modules.getNamespaces()) {
            f = installNamespace(f);
            if (f != null) {
                loadedModules.add(f);
            }
        }
    }

    public File installResource(File file, ExtensionMetadata metadata, MethodParameters... methodParams) {
        if (modulesManager != null && !modulesManager.hasFileBeenModifiedSinceLastInstalled(file)) {
            return null;
        }

        ResourceExtensionsManager extMgr = client.newServerConfigManager().newResourceExtensionsManager();
        String resourceName = getExtensionNameFromFile(file);
        if (metadata.getTitle() == null) {
            metadata.setTitle(resourceName + " resource extension");
        }

        logger.info(String.format("Loading %s resource extension from file %s", resourceName, file));
        extMgr.writeServices(resourceName, new FileHandle(file), metadata, methodParams);

        if (modulesManager != null) {
            modulesManager.saveLastInstalledTimestamp(file, new Date());
        }

        return file;
    }

    public File installTransform(File file, ExtensionMetadata metadata) {
        if (modulesManager != null && !modulesManager.hasFileBeenModifiedSinceLastInstalled(file)) {
            return null;
        }
        TransformExtensionsManager mgr = client.newServerConfigManager().newTransformExtensionsManager();
        String transformName = getExtensionNameFromFile(file);
        logger.info(String.format("Loading %s transform from file %s", transformName, file));
        if (FilenameUtil.isXslFile(file.getName())) {
            mgr.writeXSLTransform(transformName, new FileHandle(file), metadata);
        } else if (FilenameUtil.isJavascriptFile(file.getName())) {
            mgr.writeJavascriptTransform(transformName, new FileHandle(file), metadata);
        } else {
            mgr.writeXQueryTransform(transformName, new FileHandle(file), metadata);
        }

        if (modulesManager != null) {
            modulesManager.saveLastInstalledTimestamp(file, new Date());
        }

        return file;
    }

    public File installQueryOptions(File f) {
        if (modulesManager != null && !modulesManager.hasFileBeenModifiedSinceLastInstalled(f)) {
            return null;
        }
        String name = getExtensionNameFromFile(f);
        logger.info(String.format("Loading %s query options from file %s", name, f.getName()));
        QueryOptionsManager mgr = client.newServerConfigManager().newQueryOptionsManager();
        if (f.getName().endsWith(".json")) {
            mgr.writeOptions(name, new FileHandle(f).withFormat(Format.JSON));
        } else {
            mgr.writeOptions(name, new FileHandle(f));
        }

        if (modulesManager != null) {
            modulesManager.saveLastInstalledTimestamp(f, new Date());
        }

        return f;
    }

    public File installNamespace(File f) {
        if (modulesManager != null && !modulesManager.hasFileBeenModifiedSinceLastInstalled(f)) {
            return null;
        }
        String prefix = getExtensionNameFromFile(f);
        String namespaceUri = null;
        try {
            namespaceUri = FileCopyUtils.copyToString(new FileReader(f));
        } catch (IOException ie) {
            logger.error("Unable to install namespace from file: " + f.getAbsolutePath(), ie);
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

        if (modulesManager != null) {
            modulesManager.saveLastInstalledTimestamp(f, new Date());
        }
        return f;
    }

    protected String getExtensionNameFromFile(File file) {
        String name = file.getName();
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

    public void setModulesFinder(ModulesFinder extensionFilesFinder) {
        this.modulesFinder = extensionFilesFinder;
    }

    public void setModulesManager(ModulesManager configurationFilesManager) {
        this.modulesManager = configurationFilesManager;
    }

    public boolean isCatchExceptions() {
        return catchExceptions;
    }

    public void setCatchExceptions(boolean catchExceptions) {
        this.catchExceptions = catchExceptions;
    }

    public void setXccAssetLoader(XccAssetLoader xccAssetLoader) {
        this.xccAssetLoader = xccAssetLoader;
    }
}
