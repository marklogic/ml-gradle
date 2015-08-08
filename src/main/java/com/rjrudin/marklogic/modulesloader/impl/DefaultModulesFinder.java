package com.rjrudin.marklogic.modulesloader.impl;

import java.io.File;

import com.rjrudin.marklogic.modulesloader.Modules;
import com.rjrudin.marklogic.modulesloader.ModulesFinder;

/**
 * Default implementation that loads all of the different kinds of REST modules.
 */
public class DefaultModulesFinder extends BaseModulesFinder implements ModulesFinder {

    @Override
    public Modules findModules(File baseDir) {
        Modules modules = new Modules();
        addServices(modules, baseDir);
        addAssets(modules, baseDir);
        addOptions(modules, baseDir);
        addTransforms(modules, baseDir);
        addNamespaces(modules, baseDir);
        addPropertiesFile(modules, baseDir);
        return modules;
    }

}