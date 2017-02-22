package com.marklogic.client.schemasloader;

import java.io.File;
import java.util.List;
import java.util.Set;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.file.DocumentFile;

public interface SchemasLoader {

	/**
	 * Use the given DatabaseClient to load files from a particular location into the schemas database.  
	 *  Return a set containing any files that were loaded.
	 * @param directory location of schemas data
	 * @param schemasDataFinder 
	 * @param client
	 * @return a set of files that were loaded.
	 * @deprecated Prefer loadSchemas instead, which is assumed to use the new FileLoader library
	 */
	@Deprecated
    Set<File> loadSchemas(File directory, SchemasFinder schemasDataFinder, DatabaseClient client);

	/**
	 * Preferred method that assumes that the implementation handles determines which files to load at each given
	 * path, along with how to load them.
	 *
	 * @param paths
	 * @return
	 */
	List<DocumentFile> loadSchemas(String... paths);
}
