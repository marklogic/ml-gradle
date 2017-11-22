package com.marklogic.client.ext.schemasloader.impl;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.ext.batch.BatchWriter;
import com.marklogic.client.ext.file.GenericFileLoader;
import com.marklogic.client.ext.file.DocumentFile;
import com.marklogic.client.ext.modulesloader.impl.DefaultFileFilter;
import com.marklogic.client.ext.schemasloader.SchemasLoader;

import java.util.List;

public class DefaultSchemasLoader extends GenericFileLoader implements SchemasLoader {

	/**
	 * Simplest constructor for using this class. Just provide a DatabaseClient, and this will use sensible defaults
	 * for how documents are read and written. Note that the DatabaseClient will not be released after this class is
	 * done with it, as this class wasn't the one that created it.
	 *
	 * @param databaseClient
	 */
	public DefaultSchemasLoader(DatabaseClient databaseClient) {
		super(databaseClient);
		initializeDefaultSchemasLoader();
	}

	/**
	 * Assumes that the BatchWriter has already been initialized.
	 *
	 * @param batchWriter
	 */
	public DefaultSchemasLoader(BatchWriter batchWriter) {
		super(batchWriter);
		initializeDefaultSchemasLoader();
	}

	/**
	 * Adds the DocumentFileProcessors and FileFilters specific to loading schemas, which will then be used to construct
	 * a DocumentFileReader by the parent class.
	 */
	protected void initializeDefaultSchemasLoader() {
		addDocumentFileProcessor(new TdeDocumentFileProcessor());
		addFileFilter(new DefaultFileFilter());
	}

	/**
	 * Run the given paths through the DocumentFileReader, and then send the result to the BatchWriter, and then
	 * return the result.
	 *
	 * @param paths
	 * @return
	 */
	@Override
	public List<DocumentFile> loadSchemas(String... paths) {
		return super.loadFiles(paths);
	}
}
