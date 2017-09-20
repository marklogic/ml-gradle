package com.marklogic.client.ext.modulesloader.impl;

import com.marklogic.client.ext.modulesloader.Modules;
import com.marklogic.client.ext.modulesloader.ModulesFinder;

/**
 * Default implementation that loads all of the different kinds of REST modules.
 */
public class DefaultModulesFinder extends BaseModulesFinder implements ModulesFinder {

    @Override
    public Modules findModules(String baseDir) {
    	if (!baseDir.startsWith("file:") && !baseDir.startsWith("classpath")) {
    		baseDir = "file:" + baseDir;
		}
        Modules modules = new Modules();
        addServices(modules, baseDir);
        addAssetDirectories(modules, baseDir);
        addOptions(modules, baseDir);
        addTransforms(modules, baseDir);
        addNamespaces(modules, baseDir);
        addPropertiesFile(modules, baseDir);
        return modules;
    }
}
