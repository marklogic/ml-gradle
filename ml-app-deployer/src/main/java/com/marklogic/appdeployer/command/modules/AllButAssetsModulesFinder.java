/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.command.modules;

import com.marklogic.client.ext.modulesloader.Modules;
import com.marklogic.client.ext.modulesloader.impl.BaseModulesFinder;

public class AllButAssetsModulesFinder extends BaseModulesFinder {

    @Override
    protected Modules findModulesWithResolvedBaseDir(String baseDir) {
        Modules modules = new Modules();
        addOptions(modules, baseDir);
        addPropertiesFile(modules, baseDir);
        addServices(modules, baseDir);
        addTransforms(modules, baseDir);
        return modules;
    }

}
