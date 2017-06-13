package com.marklogic.client.ext.modulesloader.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.marklogic.client.ext.modulesloader.Modules;
import com.marklogic.client.ext.modulesloader.ModulesFinder;

/**
 * Default implementation that loads all of the different kinds of REST modules.
 */
public class DefaultModulesFinder extends BaseModulesFinder implements ModulesFinder {

    private ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

    @Override
    public Modules findModules(File baseDir) {
        Modules modules = new Modules();
        addServices(modules, baseDir);
        addAssetDirectories(modules, baseDir);
        addOptions(modules, baseDir);
        addTransforms(modules, baseDir);
        addNamespaces(modules, baseDir);
        addPropertiesFile(modules, baseDir);
        return modules;
    }

    /**
     * This currently uses the Client REST API to load assets instead of XCC, as the XccAssetLoader is based on
     * FileVisitor, which doesn't work for classpath resources.
     *
     * @param basePath
     * @return
     */
    public Modules findClasspathModules(String basePath) {
        try {
            Modules modules = new Modules();
            modules.setAssets(findResources(basePath, "/ext/**/*.*"));
            modules.setNamespaces(findResources(basePath, "/namespaces/*.*"));
            modules.setOptions(findResources(basePath, "/options/*.xml"));
            modules.setServices(findResources(basePath, "/services/*.xq*"));
            modules.setTransforms(findResources(basePath, "/transforms/*.xq*", "/transforms/*.xsl*"));
            return modules;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private List<Resource> findResources(String basePath, String... paths) throws IOException {
        List<Resource> list = new ArrayList<>();
        for (String path : paths) {
            Resource[] r = resolver.getResources(basePath + path);
            list.addAll(Arrays.asList(r));
        }
        return list;
    }
}
