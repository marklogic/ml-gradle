package com.marklogic.appdeployer.command;

import java.io.FilenameFilter;

/**
 * Extends FilenameFilter to add support for incremental deployments, where only resources that are new or modified since
 * a previous deployment will be deployed.
 */
public interface IncrementalFilenameFilter extends FilenameFilter {

	/**
	 * Toggle whether this file performs any incremental check.
	 *
	 * @param incrementalMode
	 */
	void setIncrementalMode(boolean incrementalMode);
}
