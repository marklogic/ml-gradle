/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.export;

import java.io.File;

/**
 * Interface for exporting one or more MarkLogic resources via the Management API to disk.
 *
 * This is located in the appdeployer package because of an assumed dependency on the ConfigDir class, which defines
 * where resources should be exported to.
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
