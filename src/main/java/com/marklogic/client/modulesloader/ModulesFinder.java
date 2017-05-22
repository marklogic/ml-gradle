package com.marklogic.client.modulesloader;

import java.io.File;

/**
 * Given a directory, return a Modules instance capturing all of the REST API modules to load.
 */
public interface ModulesFinder {

    Modules findModules(File dir);
}
