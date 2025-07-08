/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.command;

import com.marklogic.appdeployer.ConfigDir;

import java.io.File;

/**
 * Exists solely for the "findResourceDirs" method in AbstractResourceCommand.
 */
public interface ResourceDirFinder {
	File getResourceDir(ConfigDir configDir);
}
