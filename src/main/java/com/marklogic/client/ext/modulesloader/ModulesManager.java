package com.marklogic.client.ext.modulesloader;

import java.io.File;
import java.util.Date;

/**
 * Defines operations for managing whether a module needs to be installed or not.
 */
public interface ModulesManager {

    /**
     * Give the implementor a chance to initialize itself - e.g. loading data from a properties file or other resource.
     */
    void initialize();

    boolean hasFileBeenModifiedSinceLastLoaded(File file);

    void saveLastLoadedTimestamp(File file, Date date);
}
