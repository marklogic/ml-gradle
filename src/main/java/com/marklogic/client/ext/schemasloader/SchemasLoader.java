package com.marklogic.client.ext.schemasloader;

import com.marklogic.client.ext.file.DocumentFile;

import java.util.List;

public interface SchemasLoader {

	/**
	 * Assumes that the implementation handles determines which files to load at each given
	 * path, along with how to load them.
	 *
	 * @param paths
	 * @return
	 */
	List<DocumentFile> loadSchemas(String... paths);
}
