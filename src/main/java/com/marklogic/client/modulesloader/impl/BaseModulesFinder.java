package com.marklogic.client.modulesloader.impl;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.marklogic.client.helper.FilenameUtil;
import com.marklogic.client.modulesloader.Modules;
import com.marklogic.client.modulesloader.ModulesFinder;

/**
 * Abstract implementation that provides implementations for loading all the different kinds of modules, but doesn't
 * actually implement findModules.
 */
public abstract class BaseModulesFinder implements ModulesFinder {

    private FilenameFilter transformFilenameFilter = new TransformFilenameFilter();
    private FilenameFilter namespaceFilenameFilter = new NamespaceFilenameFilter();

    /**
     * Whether to treat paths that aren't recognized by this class (i.e. not services, options, namespaces, or
     * transforms) as asset paths that will then be loaded as asset modules.
     */
    private boolean includeUnrecognizedPathsAsAssetPaths = true;

    private String extPath = "ext";
    private String rootPath = "root";
    private String servicesPath = "services";
    private String optionsPath = "options";
    private String namespacesPath = "namespaces";
    private String transformsPath = "transforms";
    // special case for excluding from assets.
    private String schemasPath = "schemas";

    /**
     * Only supports JSON for now.
     * 
     * @param modules
     * @param baseDir
     */
    protected void addPropertiesFile(Modules modules, File baseDir) {
        File jsonFile = new File(baseDir, "rest-properties.json");
        if (jsonFile.exists()) {
            modules.setPropertiesFile(new FileSystemResource(jsonFile));
        }
    }

    protected void addServices(Modules modules, File baseDir) {
        File servicesBaseDir = new File(baseDir, servicesPath);
        List<Resource> services = new ArrayList<>();
        if (servicesBaseDir.exists()) {
            for (File f : servicesBaseDir.listFiles()) {
                if (FilenameUtil.isXqueryFile(f.getName()) || FilenameUtil.isJavascriptFile(f.getName())) {
                    services.add(new FileSystemResource(f));
                }
            }
        }
        modules.setServices(services);
    }

    protected void addAssetDirectories(Modules modules, File baseDir) {
        List<Resource> dirs = new ArrayList<>();
        File dir = new File(baseDir, "ext");
        if (dir.exists()) {
            dirs.add(new FileSystemResource(dir));
        }
        dir = new File(baseDir, "root");
        if (dir.exists()) {
            dirs.add(new FileSystemResource(dir));
        }

        if (includeUnrecognizedPathsAsAssetPaths && baseDir != null && baseDir.exists()) {
            List<String> recognizedPaths = getRecognizedPaths();
            for (File f : baseDir.listFiles()) {
                if (f.isDirectory() && !recognizedPaths.contains(f.getName())) {
                    dirs.add(new FileSystemResource(f));
                }
            }
        }

        modules.setAssetDirectories(dirs);
    }

    protected List<String> getRecognizedPaths() {
        return Arrays
                .asList(new String[] { extPath, rootPath, optionsPath, servicesPath, transformsPath, namespacesPath, schemasPath });
    }

    protected void addOptions(Modules modules, File baseDir) {
        File queryOptionsBaseDir = new File(baseDir, optionsPath);
        List<Resource> queryOptions = new ArrayList<>();
        if (queryOptionsBaseDir.exists()) {
            for (File f : queryOptionsBaseDir.listFiles()) {
                String filename = f.getName();
                if (filename.endsWith(".xml") || filename.endsWith(".json")) {
                    queryOptions.add(new FileSystemResource(f));
                }
            }
        }
        modules.setOptions(queryOptions);
    }

    protected void addNamespaces(Modules modules, File baseDir) {
        File namespacesDir = new File(baseDir, namespacesPath);
        List<Resource> namespaces = new ArrayList<>();
        if (namespacesDir.exists()) {
            for (File f : namespacesDir.listFiles(namespaceFilenameFilter)) {
                namespaces.add(new FileSystemResource(f));
            }
        }
        modules.setNamespaces(namespaces);
    }

    protected void addTransforms(Modules modules, File baseDir) {
        File transformsBaseDir = new File(baseDir, transformsPath);
        List<Resource> transforms = new ArrayList<>();
        if (transformsBaseDir.exists()) {
            for (File f : transformsBaseDir.listFiles(transformFilenameFilter)) {
                transforms.add(new FileSystemResource(f));
            }
        }
        modules.setTransforms(transforms);
    }

    public FilenameFilter getTransformFilenameFilter() {
        return transformFilenameFilter;
    }

    public void setTransformFilenameFilter(FilenameFilter transformFilenameFilter) {
        this.transformFilenameFilter = transformFilenameFilter;
    }

    public FilenameFilter getNamespaceFilenameFilter() {
        return namespaceFilenameFilter;
    }

    public void setNamespaceFilenameFilter(FilenameFilter namespaceFilenameFilter) {
        this.namespaceFilenameFilter = namespaceFilenameFilter;
    }

    public void setServicesPath(String servicesPath) {
        this.servicesPath = servicesPath;
    }

    public void setOptionsPath(String optionsPath) {
        this.optionsPath = optionsPath;
    }

    public void setNamespacesPath(String namespacesPath) {
        this.namespacesPath = namespacesPath;
    }

    public void setTransformsPath(String transformsPath) {
        this.transformsPath = transformsPath;
    }

    public boolean isIncludeUnrecognizedPathsAsAssetPaths() {
        return includeUnrecognizedPathsAsAssetPaths;
    }

    public void setIncludeUnrecognizedPathsAsAssetPaths(boolean includeUnrecognizedPathsAsAssetPaths) {
        this.includeUnrecognizedPathsAsAssetPaths = includeUnrecognizedPathsAsAssetPaths;
    }

    public void setExtPath(String extPath) {
        this.extPath = extPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }
}

class AssetFilenameFilter implements FilenameFilter {

    @Override
    public boolean accept(File dir, String name) {
        return !name.startsWith(".");
    }

}

class TransformFilenameFilter implements FilenameFilter {

    @Override
    public boolean accept(File dir, String name) {
        return FilenameUtil.isXslFile(name) || FilenameUtil.isXqueryFile(name) || FilenameUtil.isJavascriptFile(name);
    }
}

class NamespaceFilenameFilter implements FilenameFilter {

    @Override
    public boolean accept(File dir, String name) {
        return true;
    }
}
