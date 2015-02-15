package com.marklogic.clientutil.modulesloader;

import java.io.File;
import java.util.Set;

import com.marklogic.client.DatabaseClient;

public interface ModulesLoader {

    /**
     * Use the given DatabaseClient to load modules found in the given directory. Return a set
     * containing any files that were loaded.
     * 
     * @param directory
     * @param client
     * @return
     */
    public Set<File> loadModules(File directory, DatabaseClient client);
}
