package com.marklogic.appdeployer.command;

import java.io.File;

/**
 * Defines operations for whether a resource file should be deployed or not.
 */
public interface ResourceFileManager {

    /**
     * Give the implementor a chance to initialize itself - e.g. loading data from a properties file or other resource.
     */
    void initialize();

    boolean hasFileBeenModifiedSinceLastDeployed(File file);

    void saveLastDeployedHash(File file);
}
