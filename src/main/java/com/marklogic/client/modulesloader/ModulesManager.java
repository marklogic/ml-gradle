package com.marklogic.client.modulesloader;

import java.io.File;
import java.util.Date;

/**
 * Defines operations for managing whether a module needs to be installed or not.
 */
public interface ModulesManager {

    /**
     * Give the implementor a chance to initialize itself - e.g. loading data from a properties file or other resource.
     */
    public void initialize();

    public boolean hasFileBeenModifiedSinceLastInstalled(File file);

    public void saveLastInstalledTimestamp(File file, Date date);
}
