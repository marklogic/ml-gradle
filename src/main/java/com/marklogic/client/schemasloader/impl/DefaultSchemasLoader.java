package com.marklogic.client.schemasloader.impl;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.batch.BatchWriter;
import com.marklogic.client.ext.file.GenericFileLoader;
import com.marklogic.client.ext.file.DefaultDocumentFileReader;
import com.marklogic.client.ext.file.DocumentFile;
import com.marklogic.client.ext.file.DocumentFileReader;
import com.marklogic.client.schemasloader.SchemasLoader;

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
		initializeDocumentFileReader();
	}

	/**
	 * Assumes that the BatchWriter has already been initialized.
	 *
	 * @param batchWriter
	 */
	public DefaultSchemasLoader(BatchWriter batchWriter) {
		super(batchWriter);
		initializeDocumentFileReader();
	}

	/**
	 * Configures and sets a DefaultDocumentFileReader with a TDE-specific file processor.
	 */
	protected void initializeDocumentFileReader() {
		DefaultDocumentFileReader reader = new DefaultDocumentFileReader();
		reader.addDocumentFileProcessor(new TdeDocumentFileProcessor());
		setDocumentFileReader(reader);
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
