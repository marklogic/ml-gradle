package com.marklogic.appdeployer.command.modules;

import com.marklogic.client.ext.modulesloader.Modules;
import com.marklogic.client.ext.modulesloader.impl.BaseModulesFinder;

import java.io.File;

public class AssetModulesFinder extends BaseModulesFinder {

    @Override
    public Modules findModules(File baseDir) {
        Modules modules = new Modules();
        addAssetDirectories(modules, baseDir);
        return modules;
    }
}
