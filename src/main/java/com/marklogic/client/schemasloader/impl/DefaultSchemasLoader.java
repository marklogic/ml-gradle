package com.marklogic.client.schemasloader.impl;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.batch.BatchWriter;
import com.marklogic.client.batch.RestBatchWriter;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.document.TextDocumentManager;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.file.DefaultFileLoader;
import com.marklogic.client.helper.LoggingObject;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.schemasloader.SchemasFinder;
import com.marklogic.client.schemasloader.SchemasLoader;
import com.marklogic.client.file.DefaultDocumentFileFinder;
import com.marklogic.client.file.DocumentFile;
import com.marklogic.client.file.DocumentFileFinder;

public class DefaultSchemasLoader extends LoggingObject implements SchemasLoader {

	private DocumentFileFinder documentFileFinder = new DefaultDocumentFileFinder();
	private DatabaseClient databaseClient;

	public DefaultSchemasLoader() {
	}

	public DefaultSchemasLoader(DatabaseClient databaseClient) {
		this.databaseClient = databaseClient;
	}

	/**
	 * A RestBatchWriter is used for loading documents. The DatabaseClient is not released by this BatchWriter, as it's
	 * expected that it will be reused by the client that constructed this object.
	 *
	 * @param paths
	 * @return
	 */
	@Override
	public List<DocumentFile> loadSchemas(String... paths) {
		RestBatchWriter batchWriter = new RestBatchWriter(databaseClient);
		batchWriter.setReleaseDatabaseClients(false);
		batchWriter.initialize();
		try {
			DefaultFileLoader fileLoader = new DefaultFileLoader(batchWriter);
			fileLoader.addDocumentFileProcessor(new TdeDocumentFileProcessor());
			return fileLoader.loadFiles(paths);
		} finally {
			batchWriter.waitForCompletion();
		}
	}

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

	public void setDocumentFileFinder(DocumentFileFinder documentFileFinder) {
		this.documentFileFinder = documentFileFinder;
	}
}
