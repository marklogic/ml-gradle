package com.marklogic.appdeployer.command;

import com.marklogic.appdeployer.ConfigDir;

import java.io.File;

/**
 * Exists solely for the "findResourceDirs" method in AbstractResourceCommand.
 */
public interface ResourceDirFinder {
	File getResourceDir(ConfigDir configDir);
}
