/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.ext.schemasloader.impl;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.ext.batch.BatchWriter;
import com.marklogic.client.ext.batch.RestBatchWriter;
import com.marklogic.client.ext.file.DocumentFile;
import com.marklogic.client.ext.file.GenericFileLoader;
import com.marklogic.client.ext.modulesloader.impl.DefaultFileFilter;
import com.marklogic.client.ext.schemasloader.SchemasLoader;
import com.marklogic.client.io.DocumentMetadataHandle;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class DefaultSchemasLoader extends GenericFileLoader implements SchemasLoader {

	private final Supplier<DatabaseClient> schemasDatabaseClientSupplier;
	private final Supplier<DatabaseClient> contentDatabaseClientSupplier;
	private final boolean validateTdeTemplates;
	private final QbvDocumentFileProcessor qbvDocumentFileProcessor;

	/**
	 * @param schemasDatabaseClientSupplier supplier for loading files into an application's schemas database
	 * @param contentDatabaseClientSupplier supplier for validating TDEs and generating QBVs
	 * @param validateTdeTemplates          if false, TDEs will not be validated nor loaded via tde.templateBatchInsert
	 * @since 6.2.0
	 */
	public DefaultSchemasLoader(Supplier<DatabaseClient> schemasDatabaseClientSupplier, Supplier<DatabaseClient> contentDatabaseClientSupplier, boolean validateTdeTemplates) {
		super(schemasDatabaseClientSupplier);

		this.schemasDatabaseClientSupplier = schemasDatabaseClientSupplier;
		this.contentDatabaseClientSupplier = contentDatabaseClientSupplier;
		this.validateTdeTemplates = validateTdeTemplates;

		if (contentDatabaseClientSupplier != null) {
			this.qbvDocumentFileProcessor = new QbvDocumentFileProcessor(schemasDatabaseClientSupplier, contentDatabaseClientSupplier);
			addDocumentFileProcessor(this.qbvDocumentFileProcessor);
		} else {
			this.qbvDocumentFileProcessor = null;
		}

		addDocumentFileProcessor(new TdeDocumentFileProcessor(contentDatabaseClientSupplier));
		addFileFilter(new DefaultFileFilter());
	}

	/**
	 * @param schemasDatabaseClient for loading files into an application's schemas database
	 * @param contentDatabaseClient for validating TDEs and generating QBVs
	 * @deprecated since 6.2.0, use {@link #DefaultSchemasLoader(Supplier, Supplier, boolean)} instead
	 */
	@Deprecated
	public DefaultSchemasLoader(DatabaseClient schemasDatabaseClient, DatabaseClient contentDatabaseClient) {
		this(schemasDatabaseClient, contentDatabaseClient, true);
	}

	/**
	 * @param schemasDatabaseClient for loading files into an application's schemas database
	 * @param contentDatabaseClient for validating TDEs and generating QBVs
	 * @param validateTdeTemplates  if false, TDEs will not be validated nor loaded via tde.templateBatchInsert
	 * @deprecated since 6.2.0, use {@link #DefaultSchemasLoader(Supplier, Supplier, boolean)} instead
	 */
	@Deprecated
	public DefaultSchemasLoader(DatabaseClient schemasDatabaseClient, DatabaseClient contentDatabaseClient, boolean validateTdeTemplates) {
		super(((Supplier<BatchWriter>) () -> {
			RestBatchWriter writer = new RestBatchWriter(schemasDatabaseClient);
			// Default this to 1, as it's not typical to have such a large number of schemas to load that multiple threads
			// are needed. This also ensures that if an error occurs when loading a schema, it's thrown to the client.

			// TODO Ick - we want to retain this logic when using suppliers.
			writer.setThreadCount(1);
			writer.setReleaseDatabaseClients(false);
			return writer;
		}).get());

		this.schemasDatabaseClientSupplier = () -> schemasDatabaseClient;
		this.contentDatabaseClientSupplier = () -> contentDatabaseClient;
		this.validateTdeTemplates = validateTdeTemplates;

		if (contentDatabaseClient != null) {
			this.qbvDocumentFileProcessor = new QbvDocumentFileProcessor(this.schemasDatabaseClientSupplier, this.contentDatabaseClientSupplier);
			addDocumentFileProcessor(this.qbvDocumentFileProcessor);
		} else {
			this.qbvDocumentFileProcessor = null;
		}

		if (this.validateTdeTemplates && contentDatabaseClient != null) {
			addDocumentFileProcessor(new TdeDocumentFileProcessor(contentDatabaseClientSupplier));
		} else {
			addDocumentFileProcessor(new TdeDocumentFileProcessor((Supplier<DatabaseClient>) null));
		}

		addFileFilter(new DefaultFileFilter());
	}

	/**
	 * Run the given paths through the DocumentFileReader, and then send the result to the BatchWriter, and then return
	 * the result.
	 *
	 * @param paths
	 * @return a DocumentFile for each file that was loaded as a schema
	 */
	@Override
	public List<DocumentFile> loadSchemas(String... paths) {
		final List<DocumentFile> documentFiles = super.getDocumentFiles(paths);

		if (!documentFiles.isEmpty()) {
			final DatabaseClient schemasClient = schemasDatabaseClientSupplier.get();
			final DatabaseClient contentClient = contentDatabaseClientSupplier.get();

			if (this.validateTdeTemplates && TdeUtil.templateBatchInsertSupported(schemasClient) && contentClient != null) {
				SchemaFiles schemaFiles = readSchemaFiles(documentFiles);
				if (!schemaFiles.tdeFiles.isEmpty()) {
					loadTdeTemplatesViaBatchInsert(schemaFiles.tdeFiles, contentClient);
				}
				if (!schemaFiles.nonTdeFiles.isEmpty()) {
					if (logger.isDebugEnabled()) {
						logger.debug("Non-TDE files: {}", schemaFiles.nonTdeFiles);
					}
					super.writeDocumentFiles(schemaFiles.nonTdeFiles);
				}
			} else {
				writeDocumentFiles(documentFiles);
			}
		}

		if (this.qbvDocumentFileProcessor != null) {
			this.qbvDocumentFileProcessor.processQbvFiles();
		}

		return documentFiles;
	}

	/**
	 * @param documentFiles
	 * @return a SchemaFiles instance that captures a list of TDE files (if any) and a list of all other files found
	 * that are not TDEs (may also be empty).
	 */
	private SchemaFiles readSchemaFiles(List<DocumentFile> documentFiles) {
		List<DocumentFile> tdeFiles = new ArrayList<>();
		List<DocumentFile> nonTdeFiles = new ArrayList<>();

		for (DocumentFile file : documentFiles) {
			DocumentMetadataHandle metadata = file.getMetadata();
			if (metadata != null && metadata.getCollections().contains(TdeUtil.TDE_COLLECTION)) {
				tdeFiles.add(file);
			} else {
				nonTdeFiles.add(file);
			}
		}
		return new SchemaFiles(tdeFiles, nonTdeFiles);
	}

	private void loadTdeTemplatesViaBatchInsert(List<DocumentFile> tdeFiles, DatabaseClient contentDatabaseClient) {
		logger.info("Loading and validating TDE templates via tde.templateBatchInsert; templates: {}",
			tdeFiles.stream().map(documentFile -> documentFile.getFile().getName()).collect(Collectors.toList()));

		String query = buildTdeBatchInsertQuery(tdeFiles);
		StringBuilder script = new StringBuilder("declareUpdate(); const tde = require('/MarkLogic/tde.xqy'); ");
		script.append(query);
		try {
			contentDatabaseClient.newServerEval().javascript(script.toString()).eval().close();
		} catch (Exception ex) {
			throw new RuntimeException("Unable to load and validate TDE templates via tde.templateBatchInsert; " +
				"cause: " + ex.getMessage() + "; the following script can be run in Query Console against your content " +
				"database to see the TDE validation error:\n" + script, ex);
		}
	}

	/**
	 * @param documentFiles
	 * @return a JavaScript query that uses the tde.templateBatchInsert function introduced in ML 10.0-9
	 */
	private String buildTdeBatchInsertQuery(List<DocumentFile> documentFiles) {
		List<String> templateInfoList = new ArrayList<>();
		for (DocumentFile doc : documentFiles) {
			String uri = doc.getUri();
			String content = doc.getContent().toString();

			// Permissions
			DocumentMetadataHandle.DocumentPermissions documentPermissions = doc.getDocumentMetadata().getPermissions();
			List<String> permissionList = new ArrayList<>();
			documentPermissions.keySet().forEach(key -> {
				Set<DocumentMetadataHandle.Capability> values = documentPermissions.get(key);
				values.forEach(value -> permissionList.add(String.format("xdmp.permission('%s', '%s')", key, value.toString().toLowerCase())));
			});
			String permissions = "[".concat(permissionList.stream().collect(Collectors.joining(", "))).concat("]");

			// Collections
			List<String> collectionsList = new ArrayList<>();
			doc.getDocumentMetadata().getCollections().forEach(collection -> collectionsList.add(collection));
			String collections = collectionsList.stream().map(coll -> '"' + coll + '"').collect(Collectors.joining(", "));
			collections = "[".concat(collections).concat("]");

			// Template info
			String templateFormat = "";
			if (doc.getFormat().toString().equals("XML")) {
				templateFormat = String.format("tde.templateInfo('%s', xdmp.unquote(`%s`), %s, %s)", uri, content, permissions, collections);
			} else if (doc.getFormat().toString().equals("JSON")) {
				templateFormat = String.format("tde.templateInfo('%s', xdmp.toJSON(%s), %s, %s)", uri, content, permissions, collections);
			} else {
				templateFormat = String.format("tde.templateInfo('%s',%s, %s, %s)", uri, content, permissions, collections);
			}
			templateInfoList.add(templateFormat);
		}

		String templateString = "tde.templateBatchInsert(["
			.concat(templateInfoList.stream().collect(Collectors.joining(",")))
			.concat("]);");
		return templateString;
	}

	private record SchemaFiles(List<DocumentFile> tdeFiles, List<DocumentFile> nonTdeFiles) {
	}
}
