package com.marklogic.client.ext.modulesloader;

/**
 * Given a directory, return a Modules instance capturing all of the REST API modules to load.
 */
public interface ModulesFinder {

    Modules findModules(String path);
}
