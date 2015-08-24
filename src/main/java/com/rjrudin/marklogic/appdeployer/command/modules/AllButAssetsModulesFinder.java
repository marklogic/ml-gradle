package com.rjrudin.marklogic.appdeployer.command.modules;

import java.io.File;

import com.rjrudin.marklogic.modulesloader.Modules;
import com.rjrudin.marklogic.modulesloader.impl.BaseModulesFinder;

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
