package com.marklogic.appdeployer.command;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Extends FilenameFilter to add support for incremental deployments, where only resources that are new or modified since
 * a previous deployment will be deployed.
 */
public interface IncrementalFilenameFilter extends FilenameFilter {

	/**
	 * If the given File is processed during a deployment, do not perform an incremental check on it - i.e. essentially
	 * act as though incremental mode is disabled.
	 *
	 * @param resourceFile
	 */
	void ignoreIncrementalCheckForFile(File resourceFile);

	/**
	 * Toggle whether this file performs any incremental check.
	 *
	 * @param incrementalMode
	 */
	void setIncrementalMode(boolean incrementalMode);
}
