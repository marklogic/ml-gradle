package com.marklogic.appdeployer.export;

import java.io.File;

/**
 * Interface for exporting one or more MarkLogic resources via the Management API to disk.
 */
public interface ResourceExporter {

	String FORMAT_XML = "xml";
	String FORMAT_JSON = "json";

	/**
	 * @param baseDir
	 * @return
	 */
	ExportedResources exportResources(File baseDir);
}
