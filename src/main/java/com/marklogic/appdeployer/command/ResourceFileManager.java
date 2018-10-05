package com.marklogic.appdeployer.command;

import java.io.File;

/**
 * Defines operations for whether a resource file should be processed or not during a deployment.
 */
public interface ResourceFileManager {

	/**
	 * Give the implementor a chance to initialize itself - e.g. loading data from a properties file or other resource.
	 */
	void initialize();

	/**
	 * @param file
	 * @return
	 */
	boolean shouldResourceFileBeProcessed(File file);
}
