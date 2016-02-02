package com.marklogic.client.schemasloader;

import java.io.File;
import java.util.Set;

import com.marklogic.client.DatabaseClient;

public interface SchemasLoader {

	/**
	 * Use the given DatabaseClient to load files from a particular location into the schemas database.  
	 *  Return a set containing any files that were loaded.
	 * @param directory location of schemas data
	 * @param schemasDataFinder 
	 * @param client
	 * @return a set of files that were loaded.
	 */
    public Set<File> loadSchemas(File directory, SchemasFinder schemasDataFinder, DatabaseClient client);

}
