package com.rjrudin.marklogic.appdeployer.command.modules;

import java.io.File;

import com.rjrudin.marklogic.modulesloader.Modules;
import com.rjrudin.marklogic.modulesloader.impl.BaseModulesFinder;

public class AssetModulesFinder extends BaseModulesFinder {

    @Override
    public Modules findModules(File baseDir) {
        Modules modules = new Modules();
        addAssetDirectories(modules, baseDir);
        return modules;
    }
}
