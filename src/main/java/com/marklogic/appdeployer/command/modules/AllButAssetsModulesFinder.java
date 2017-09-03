package com.marklogic.appdeployer.command.modules;

import com.marklogic.client.ext.modulesloader.Modules;
import com.marklogic.client.ext.modulesloader.impl.BaseModulesFinder;

import java.io.File;

public class AllButAssetsModulesFinder extends BaseModulesFinder {

    @Override
    public Modules findModules(File baseDir) {
        Modules modules = new Modules();
        addNamespaces(modules, baseDir);
        addOptions(modules, baseDir);
        addPropertiesFile(modules, baseDir);
        addServices(modules, baseDir);
        addTransforms(modules, baseDir);
        return modules;
    }

}
