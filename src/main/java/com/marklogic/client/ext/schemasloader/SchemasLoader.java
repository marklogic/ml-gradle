package com.marklogic.client.ext.schemasloader;

import com.marklogic.client.ext.file.DocumentFile;

import java.util.List;

public interface SchemasLoader {

	/**
	 * Assumes that the implementation handles determines which files to load at each given
	 * path, along with how to load them.
	 *
	 * @param paths
	 * @return a DocumentFile for each file that was loaded from the given paths
	 */
	List<DocumentFile> loadSchemas(String... paths);
}
