package com.marklogic.appdeployer.command.modules;

import java.io.File;

import com.marklogic.client.modulesloader.Modules;
import com.marklogic.client.modulesloader.impl.BaseModulesFinder;

public class AssetModulesFinder extends BaseModulesFinder {

    @Override
    public Modules findModules(File baseDir) {
        Modules modules = new Modules();
        addAssetDirectories(modules, baseDir);
        return modules;
    }
}
