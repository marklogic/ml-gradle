package com.marklogic.client.ext.modulesloader.impl;

import com.marklogic.client.ext.file.DocumentFile;

import java.util.List;

/**
 * Interface for performing static checks on a list of modules that have been loaded into MarkLogic already.
 */
public interface StaticChecker {

	void checkLoadedAssets(List<DocumentFile> documentFiles);
}
