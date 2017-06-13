package com.marklogic.client.ext.modulesloader;

import java.io.File;
import java.util.Set;

import com.marklogic.client.DatabaseClient;

/**
 * Interface for objects that can load a set of modules via the REST API, which is intended to include not just what the
 * REST API calls "assets" (regular modules), but also options, services, transforms, and namespaces.
 */
public interface ModulesLoader {

    /**
     * Use the given DatabaseClient to load modules found in the given directory. Return a set containing any files that
     * were loaded.
     *
     * @param directory
     * @param modulesFinder
     * @param client
     * @return
     */
    Set<File> loadModules(File directory, ModulesFinder modulesFinder, DatabaseClient client);
}
