package com.marklogic.client.ext.modulesloader.impl;

import java.io.File;

import com.marklogic.client.ext.modulesloader.Modules;

/**
 * Search options and REST properties are specific to a REST API server. So if you a REST API server for testing
 * purposes which is different from the REST API server that you use in an application, you'll most likely want to load
 * the options and REST properties via the test server as well. This ModulesFinder then only returns options and a
 * properties file, if they exist.
 */
public class TestServerModulesFinder extends BaseModulesFinder {

    @Override
    public Modules findModules(File baseDir) {
        Modules modules = new Modules();
        addOptions(modules, baseDir);
        addPropertiesFile(modules, baseDir);
        return modules;
    }

}
