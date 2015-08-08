package com.rjrudin.marklogic.modulesloader;

import java.io.File;
import java.util.List;

public class Modules {

    private List<File> services;
    private List<Asset> assets;
    private List<File> transforms;
    private List<File> options;
    private List<File> namespaces;
    private File propertiesFile;

    public List<File> getServices() {
        return services;
    }

    public List<Asset> getAssets() {
        return assets;
    }

    public List<File> getTransforms() {
        return transforms;
    }

    public List<File> getOptions() {
        return options;
    }

    public void setServices(List<File> resources) {
        this.services = resources;
    }

    public void setAssets(List<Asset> assets) {
        this.assets = assets;
    }

    public void setTransforms(List<File> transforms) {
        this.transforms = transforms;
    }

    public void setOptions(List<File> queryOptions) {
        this.options = queryOptions;
    }

    public List<File> getNamespaces() {
        return namespaces;
    }

    public void setNamespaces(List<File> namespaces) {
        this.namespaces = namespaces;
    }

    public File getPropertiesFile() {
        return propertiesFile;
    }

    public void setPropertiesFile(File propertiesFile) {
        this.propertiesFile = propertiesFile;
    }

}
