package com.marklogic.appdeployer.export;

import java.io.File;
import java.util.List;

/**
 * Interface for exporting one or more MarkLogic resources via the Management API to disk.
 */
public interface ResourceExporter {

	/**
	 * @param baseDir
	 * @return a list of Files, one for each resource that was exported; expectation is this will mostly be for reporting
	 * reasons
	 */
	List<File> exportResources(File baseDir);
}
