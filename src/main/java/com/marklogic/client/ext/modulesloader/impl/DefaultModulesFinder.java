package com.marklogic.client.ext.modulesloader.impl;

import com.marklogic.client.ext.modulesloader.Modules;
import com.marklogic.client.ext.modulesloader.ModulesFinder;


/**
 * Default implementation that loads all of the different kinds of REST modules.
 */
public class DefaultModulesFinder extends BaseModulesFinder implements ModulesFinder {

    @Override
    protected Modules findModulesWithResolvedBaseDir(String baseDir) {
        Modules modules = new Modules();
	    addAssetDirectories(modules, baseDir);
        addServices(modules, baseDir);
        addOptions(modules, baseDir);
        addTransforms(modules, baseDir);
        addNamespaces(modules, baseDir);
        addPropertiesFile(modules, baseDir);
        return modules;
    }
}
