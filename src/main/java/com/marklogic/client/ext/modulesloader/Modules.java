package com.marklogic.client.ext.modulesloader;

import java.util.List;

import org.springframework.core.io.Resource;

public class Modules {

    private List<Resource> assets;
    private List<Resource> services;
    private List<Resource> assetDirectories;
    private List<Resource> transforms;
    private List<Resource> options;
    private List<Resource> namespaces;
    private Resource propertiesFile;

    public List<Resource> getServices() {
        return services;
    }

    public List<Resource> getTransforms() {
        return transforms;
    }

    public List<Resource> getOptions() {
        return options;
    }

    public void setServices(List<Resource> resources) {
        this.services = resources;
    }

    public void setTransforms(List<Resource> transforms) {
        this.transforms = transforms;
    }

    public void setOptions(List<Resource> queryOptions) {
        this.options = queryOptions;
    }

    public List<Resource> getNamespaces() {
        return namespaces;
    }

    public void setNamespaces(List<Resource> namespaces) {
        this.namespaces = namespaces;
    }

    public Resource getPropertiesFile() {
        return propertiesFile;
    }

    public void setPropertiesFile(Resource propertiesFile) {
        this.propertiesFile = propertiesFile;
    }

    public List<Resource> getAssetDirectories() {
        return assetDirectories;
    }

    public void setAssetDirectories(List<Resource> assetDirectories) {
        this.assetDirectories = assetDirectories;
    }

    public List<Resource> getAssets() {
        return assets;
    }

    public void setAssets(List<Resource> assets) {
        this.assets = assets;
    }

}
