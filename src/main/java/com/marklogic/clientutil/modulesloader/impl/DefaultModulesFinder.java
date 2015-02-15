package com.marklogic.clientutil.modulesloader.impl;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import com.marklogic.clientutil.FilenameUtil;
import com.marklogic.clientutil.modulesloader.Asset;
import com.marklogic.clientutil.modulesloader.Modules;
import com.marklogic.clientutil.modulesloader.ModulesFinder;

public class DefaultModulesFinder implements ModulesFinder {

    private FilenameFilter assetFilenameFilter = new AssetFilenameFilter();
    private FilenameFilter transformFilenameFilter = new TransformFilenameFilter();
    private FilenameFilter namespaceFilenameFilter = new NamespaceFilenameFilter();

    @Override
    public Modules findModules(File baseDir) {
        Modules modules = new Modules();
        addServices(modules, baseDir);
        addAssets(modules, baseDir);
        addOptions(modules, baseDir);
        addTransforms(modules, baseDir);
        addNamespaces(modules, baseDir);
        return modules;
    }

    protected void addServices(Modules modules, File baseDir) {
        File servicesBaseDir = new File(baseDir, "services");
        List<File> services = new ArrayList<>();
        if (servicesBaseDir.exists()) {
            for (File f : servicesBaseDir.listFiles()) {
                if (FilenameUtil.isXqueryFile(f.getName())) {
                    services.add(f);
                }
            }
        }
        modules.setServices(services);
    }

    protected void addAssets(Modules modules, File baseDir) {
        File assetsBaseDir = new File(baseDir, "ext");
        List<Asset> assets = new ArrayList<>();
        addAssetFiles(assetsBaseDir, "", assets);
        modules.setAssets(assets);
    }

    protected void addAssetFiles(File dir, String pathRelativeToAssetsDir, List<Asset> assets) {
        if (dir.exists()) {
            for (File f : dir.listFiles(getAssetFilenameFilter())) {
                String name = f.getName();
                if (f.isDirectory()) {
                    addAssetFiles(f, pathRelativeToAssetsDir + "/" + name, assets);
                } else if (f.isFile()) {
                    assets.add(new Asset(f, pathRelativeToAssetsDir + "/" + name));
                }
            }
        }
    }

    protected void addOptions(Modules modules, File baseDir) {
        File queryOptionsBaseDir = new File(baseDir, "options");
        List<File> queryOptions = new ArrayList<>();
        if (queryOptionsBaseDir.exists()) {
            for (File f : queryOptionsBaseDir.listFiles()) {
                String filename = f.getName();
                if (filename.endsWith(".xml") || filename.endsWith(".json")) {
                    queryOptions.add(f);
                }
            }
        }
        modules.setOptions(queryOptions);
    }

    protected void addNamespaces(Modules modules, File baseDir) {
        File namespacesDir = new File(baseDir, "namespaces");
        List<File> namespaces = new ArrayList<>();
        if (namespacesDir.exists()) {
            for (File f : namespacesDir.listFiles(namespaceFilenameFilter)) {
                namespaces.add(f);
            }
        }
        modules.setNamespaces(namespaces);
    }

    protected void addTransforms(Modules modules, File baseDir) {
        File transformsBaseDir = new File(baseDir, "transforms");
        List<File> transforms = new ArrayList<>();
        if (transformsBaseDir.exists()) {
            for (File f : transformsBaseDir.listFiles(transformFilenameFilter)) {
                transforms.add(f);
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

    public FilenameFilter getAssetFilenameFilter() {
        return assetFilenameFilter;
    }

    public void setAssetFilenameFilter(FilenameFilter assetFilenameFilter) {
        this.assetFilenameFilter = assetFilenameFilter;
    }

    public FilenameFilter getNamespaceFilenameFilter() {
        return namespaceFilenameFilter;
    }

    public void setNamespaceFilenameFilter(FilenameFilter namespaceFilenameFilter) {
        this.namespaceFilenameFilter = namespaceFilenameFilter;
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
        return FilenameUtil.isXslFile(name) || FilenameUtil.isXqueryFile(name);
    }

}

class NamespaceFilenameFilter implements FilenameFilter {

    @Override
    public boolean accept(File dir, String name) {
        return true;
    }

}
