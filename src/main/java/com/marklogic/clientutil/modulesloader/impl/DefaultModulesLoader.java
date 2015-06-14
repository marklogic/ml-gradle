package com.marklogic.clientutil.modulesloader.impl;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.springframework.util.FileCopyUtils;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.admin.ExtensionLibrariesManager;
import com.marklogic.client.admin.ExtensionLibraryDescriptor;
import com.marklogic.client.admin.ExtensionMetadata;
import com.marklogic.client.admin.NamespacesManager;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.admin.ResourceExtensionsManager;
import com.marklogic.client.admin.ResourceExtensionsManager.MethodParameters;
import com.marklogic.client.admin.TransformExtensionsManager;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.Format;
import com.marklogic.clientutil.FilenameUtil;
import com.marklogic.clientutil.LoggingObject;
import com.marklogic.clientutil.modulesloader.Asset;
import com.marklogic.clientutil.modulesloader.ExtensionLibraryDescriptorBuilder;
import com.marklogic.clientutil.modulesloader.ExtensionMetadataAndParams;
import com.marklogic.clientutil.modulesloader.ExtensionMetadataProvider;
import com.marklogic.clientutil.modulesloader.Modules;
import com.marklogic.clientutil.modulesloader.ModulesFinder;
import com.marklogic.clientutil.modulesloader.ModulesManager;

public class DefaultModulesLoader extends LoggingObject implements com.marklogic.clientutil.modulesloader.ModulesLoader {

    private DatabaseClient client;

    private ExtensionMetadataProvider extensionMetadataProvider;
    private ModulesFinder modulesFinder;
    private ModulesManager modulesManager;
    private ExtensionLibraryDescriptorBuilder extensionLibraryDescriptorBuilder;

    /**
     * When set to true, exceptions thrown while loading transforms and resources will be caught and logged, and the
     * module will be updated as having been loaded. This is useful when running a program like ModulesWatcher, as it
     * prevents the program from crashing and also from trying to load the module over and over.
     */
    private boolean catchExceptions = false;

    public DefaultModulesLoader() {
        this.extensionMetadataProvider = new DefaultExtensionMetadataProvider();
        this.modulesFinder = new DefaultModulesFinder();
        this.modulesManager = new PropertiesModuleManager();
    }

    public Set<File> loadModules(File baseDir, DatabaseClient client) {
        setDatabaseClient(client);

        if (modulesManager != null) {
            modulesManager.initialize();
        }

        Modules modules = modulesFinder.findModules(baseDir);

        Set<File> loadedModules = new HashSet<>();

        loadAssets(modules, loadedModules);
        loadQueryOptions(modules, loadedModules);
        loadTransforms(modules, loadedModules);
        loadResources(modules, loadedModules);
        loadNamespaces(modules, loadedModules);

        return loadedModules;
    }

    protected void loadAssets(Modules modules, Set<File> loadedModules) {
        for (Asset asset : modules.getAssets()) {
            File f = installAsset(asset);
            if (f != null) {
                loadedModules.add(f);
            }
        }
    }

    protected void loadQueryOptions(Modules modules, Set<File> loadedModules) {
        for (File f : modules.getOptions()) {
            f = installQueryOptions(f);
            if (f != null) {
                loadedModules.add(f);
            }
        }
    }

    protected void loadTransforms(Modules modules, Set<File> loadedModules) {
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
        for (File f : modules.getNamespaces()) {
            f = installNamespace(f);
            if (f != null) {
                loadedModules.add(f);
            }
        }
    }

    /**
     * This can be used by projects that use MLCP to load many modules in the assets directory. In such a case, it's
     * usually desirable to pretend to load all of the assets so that the timestamp at which each asset was last loaded
     * is updated to the current time.
     *
     * @param baseDir
     */
    public void simulateLoadingOfAllAssets(File baseDir, DatabaseClient client) {
        setDatabaseClient(client);
        Date now = new Date();

        if (modulesManager != null) {
            modulesManager.initialize();
        }

        Modules files = modulesFinder.findModules(baseDir);

        if (modulesManager != null) {
            for (Asset asset : files.getAssets()) {
                modulesManager.saveLastInstalledTimestamp(asset.getFile(), now);
            }
        }
    }

    public File installAsset(Asset asset) {
        File file = asset.getFile();

        if (modulesManager != null & !modulesManager.hasFileBeenModifiedSinceLastInstalled(file)) {
            return null;
        }
        ExtensionLibrariesManager libMgr = client.newServerConfigManager().newExtensionLibrariesManager();
        Format format = determineFormat(file);
        FileHandle h = new FileHandle(file);
        if (extensionLibraryDescriptorBuilder != null) {
            ExtensionLibraryDescriptor descriptor = extensionLibraryDescriptorBuilder.buildDescriptor(asset);
            logger.info(String.format("Loading module at path %s from file %s", descriptor.getPath(),
                    file.getAbsolutePath()));
            try {
                libMgr.write(descriptor, h.withFormat(format));
            } catch (FailedRequestException fre) {
                logger.warn("Caught exception, retrying as binary file; exception message: " + fre.getMessage());
                libMgr.write(descriptor, h.withFormat(Format.BINARY));
            }
        } else {
            String path = "/ext" + asset.getPath();
            logger.info(String.format("Loading module at path %s from file %s", path, file.getAbsolutePath()));
            try {
                libMgr.write(path, h.withFormat(format));
            } catch (FailedRequestException fre) {
                logger.warn("Caught exception, retrying as binary file; exception message: " + fre.getMessage());
                libMgr.write(path, h.withFormat(Format.BINARY));
            }
        }

        if (modulesManager != null) {
            modulesManager.saveLastInstalledTimestamp(file, new Date());
        }

        return file;
    }

    /**
     * TODO Need something pluggable here - probably should delegate this to a separate object so that a client could
     * easily provide a different implementation in case the assumptions below aren't correct.
     *
     * @param file
     * @return
     */
    protected Format determineFormat(File file) {
        String name = file.getName();
        if (FilenameUtil.isXslFile(name) || name.endsWith(".xml") || name.endsWith(".html")) {
            return Format.XML;
        } else if (name.endsWith(".swf") || name.endsWith(".jpeg") || name.endsWith(".jpg") || name.endsWith(".png")
                || name.endsWith(".gif") || name.endsWith(".svg") || name.endsWith(".ttf") || name.endsWith(".eot")
                || name.endsWith(".woff") || name.endsWith(".cur")) {
            return Format.BINARY;
        }
        return Format.TEXT;
    }

    public File installResource(File file, ExtensionMetadata metadata, MethodParameters... methodParams) {
        if (modulesManager != null & !modulesManager.hasFileBeenModifiedSinceLastInstalled(file)) {
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

    public void setConfigurationFilesFinder(ModulesFinder extensionFilesFinder) {
        this.modulesFinder = extensionFilesFinder;
    }

    public void setConfigurationFilesManager(ModulesManager configurationFilesManager) {
        this.modulesManager = configurationFilesManager;
    }

    public void setExtensionLibraryDescriptorBuilder(ExtensionLibraryDescriptorBuilder extensionLibraryDescriptorBuilder) {
        this.extensionLibraryDescriptorBuilder = extensionLibraryDescriptorBuilder;
    }

    public boolean isCatchExceptions() {
        return catchExceptions;
    }

    public void setCatchExceptions(boolean catchExceptions) {
        this.catchExceptions = catchExceptions;
    }

}
