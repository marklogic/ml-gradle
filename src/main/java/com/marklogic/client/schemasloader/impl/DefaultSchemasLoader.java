package com.marklogic.client.schemasloader.impl;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.batch.BatchWriter;
import com.marklogic.client.batch.RestBatchWriter;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.document.TextDocumentManager;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.file.DefaultDocumentFileReader;
import com.marklogic.client.file.DocumentFile;
import com.marklogic.client.file.DocumentFileReader;
import com.marklogic.client.helper.LoggingObject;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.schemasloader.SchemasFinder;
import com.marklogic.client.schemasloader.SchemasLoader;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DefaultSchemasLoader extends LoggingObject implements SchemasLoader {

	private DocumentFileReader documentFileReader;
	private BatchWriter batchWriter;
	private boolean waitForCompletion = true;

	/**
	 * @deprecated This constructor is only for the deprecated way of loading schemas
	 */
	public DefaultSchemasLoader() {
	}

	/**
	 * Simplest constructor for using this class. Just provide a DatabaseClient, and this will use sensible defaults
	 * for how documents are read and written. Note that the DatabaseClient will not be released after this class is
	 * done with it, as this class wasn't the one that created it.
	 *
	 * @param databaseClient
	 */
	public DefaultSchemasLoader(DatabaseClient databaseClient) {
		RestBatchWriter restBatchWriter = new RestBatchWriter(databaseClient);
		restBatchWriter.setReleaseDatabaseClients(false);
		restBatchWriter.initialize();
		this.batchWriter = restBatchWriter;

		DefaultDocumentFileReader reader = new DefaultDocumentFileReader();
		reader.addDocumentFileProcessor(new TdeDocumentFileProcessor());
		this.documentFileReader = reader;
	}

	/**
	 * Assumes that the BatchWriter has already been initialized.
	 *
	 * @param documentFileReader
	 * @param batchWriter
	 */
	public DefaultSchemasLoader(DocumentFileReader documentFileReader, BatchWriter batchWriter) {
		this.documentFileReader = documentFileReader;
		this.batchWriter = batchWriter;
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
		List<DocumentFile> documentFiles = documentFileReader.readDocumentFiles(paths);
		if (documentFiles != null && !documentFiles.isEmpty()) {
			if (logger.isInfoEnabled()) {
				logger.info(format("Writing %d files into the schemas database", documentFiles.size()));
			}
			batchWriter.write(documentFiles);
			if (waitForCompletion) {
				batchWriter.waitForCompletion();
			}
		}
		return documentFiles;
	}

	/**
	 * @param baseDir
	 * @param schemasDataFinder
	 * @param client
	 * @return
	 * @deprecated
	 */
	@Override
	public Set<File> loadSchemas(File baseDir, SchemasFinder schemasDataFinder, DatabaseClient client) {
		XMLDocumentManager xmlDocMgr = client.newXMLDocumentManager();
		TextDocumentManager textDocMgr = client.newTextDocumentManager();
		JSONDocumentManager jsonDocMgr = client.newJSONDocumentManager();

		List<File> schemasData = schemasDataFinder.findSchemas(baseDir);

		Set<File> loadedSchemas = new HashSet<>();
		DocumentMetadataHandle tdeCollection = new DocumentMetadataHandle()
			.withCollections("http://marklogic.com/xdmp/tde");
		for (File f : schemasData) {
			String extension = getExtensionNameFromFile(f);
			FileHandle handle = new FileHandle(f);
			if (extension.equals("tdej")) {
				jsonDocMgr.write(f.getName(), tdeCollection, handle.withFormat(Format.JSON));
			} else if (extension.equals("tdex")) {
				xmlDocMgr.write(f.getName(), tdeCollection, handle.withFormat(Format.XML));
			} else if (extension.equals("xsd")) {
				xmlDocMgr.write(f.getName(), handle.withFormat(Format.XML));
			} else {
				textDocMgr.write(f.getName(), handle.withFormat(Format.TEXT));
			}
			loadedSchemas.add(f);
		}

		return loadedSchemas;
	}

	protected String getExtensionNameFromFile(File file) {
		String name = file.getName();
		int pos = name.lastIndexOf('.');
		return pos < 0 ? name : name.substring(pos + 1);
	}

	public void setWaitForCompletion(boolean waitForCompletion) {
		this.waitForCompletion = waitForCompletion;
	}

	public DocumentFileReader getDocumentFileReader() {
		return documentFileReader;
	}

	public BatchWriter getBatchWriter() {
		return batchWriter;
	}
}
