/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.ext.schemasloader.impl;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.eval.ServerEvaluationCall;
import com.marklogic.client.ext.file.DocumentFile;
import com.marklogic.client.ext.file.DocumentFileProcessor;
import com.marklogic.client.ext.helper.FilenameUtil;
import com.marklogic.client.ext.helper.LoggingObject;
import com.marklogic.client.ext.util.XmlUtil;
import com.marklogic.client.extra.jdom.JDOMHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import org.jdom2.Document;
import org.jdom2.Element;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * @since 4.6.0
 */
class QbvDocumentFileProcessor extends LoggingObject implements DocumentFileProcessor {

	public static final String QBV_COLLECTION = "http://marklogic.com/xdmp/qbv";
	private static final String QBV_XML_PLAN_NAMESPACE = "http://marklogic.com/plan";
	private static final String QBV_XML_ROOT_ELEMENT = "query-based-view";

	final private Supplier<DatabaseClient> schemasDatabaseClientSupplier;
	final private Supplier<DatabaseClient> contentDatabaseClientSupplier;
	final private List<DocumentFile> qbvFiles = new ArrayList<>();

	/**
	 * @param schemasDatabaseClientSupplier supplier used to write the QBV XML document to the application's schemas database
	 * @param contentDatabaseClientSupplier supplier used to generate the QBV based on a user-provided script
	 * @since 6.2.0
	 */
	QbvDocumentFileProcessor(Supplier<DatabaseClient> schemasDatabaseClientSupplier, Supplier<DatabaseClient> contentDatabaseClientSupplier) {
		this.schemasDatabaseClientSupplier = schemasDatabaseClientSupplier;
		this.contentDatabaseClientSupplier = contentDatabaseClientSupplier;
	}

	/**
	 * @param schemasDatabaseClient used to write the QBV XML document to the application's schemas database
	 * @param contentDatabaseClient used to generate the QBV based on a user-provided script
	 * @deprecated since 6.2.0, use {@link #QbvDocumentFileProcessor(Supplier, Supplier)} instead
	 */
	@Deprecated
	QbvDocumentFileProcessor(DatabaseClient schemasDatabaseClient, DatabaseClient contentDatabaseClient) {
		this(() -> schemasDatabaseClient, () -> contentDatabaseClient);
	}

	@Override
	public DocumentFile processDocumentFile(DocumentFile documentFile) {
		if (isQbvQuery(documentFile)) {
			// Defer processing until after all other schemas have been processed.
			qbvFiles.add(documentFile);
			return null;
		} else {
			return documentFile;
		}
	}

	private boolean isQbvQuery(DocumentFile documentFile) {
		String uri = documentFile.getUri();
		File file = documentFile.getFile();
		return uri != null
			&& uri.startsWith("/qbv")
			&& file != null
			&& (FilenameUtil.isXqueryFile(file.getName()) || FilenameUtil.isJavascriptFile(file.getName()));
	}

	public void processQbvFiles() {
		qbvFiles.forEach(this::processQbvFile);
		qbvFiles.clear();
	}

	private void processQbvFile(DocumentFile qbvFile) {
		if (logger.isInfoEnabled()) {
			logger.info("Generating Query-Based View for file: {}", qbvFile.getFile().getName());
		}

		ServerEvaluationCall call = getServerEvaluationCall(qbvFile, contentDatabaseClientSupplier.get());
		if (call != null) {
			StringHandle handleString = new StringHandle();
			try {
				call.eval(handleString);
			} catch (Exception e) {
				throw new RuntimeException(format("Query-Based View generation failed for file: %s; cause: %s",
					qbvFile.getFile().getAbsolutePath(), e.getMessage()));
			}

			if (Format.XML.equals(handleString.getFormat())) {
				Document xmlDocument;
				try {
					xmlDocument = XmlUtil.newSAXBuilder().build(new StringReader(handleString.get()));
				} catch (Exception e) {
					throw new RuntimeException(format("Query-Based View generation failed for file: %s; cause: %s", qbvFile.getFile().getAbsolutePath(), e.getMessage()));
				}
				Element root = xmlDocument.getRootElement();
				if (QBV_XML_ROOT_ELEMENT.equals(root.getName()) & (root.getNamespace() != null && root.getNamespace().getURI().equals(QBV_XML_PLAN_NAMESPACE))) {
					qbvFile.getDocumentMetadata().getCollections().add(QBV_COLLECTION);
					String uri = qbvFile.getUri() + ".xml";
					XMLDocumentManager schemasDocumentManager = schemasDatabaseClientSupplier.get().newXMLDocumentManager();
					schemasDocumentManager.write(uri, qbvFile.getDocumentMetadata(), new JDOMHandle(xmlDocument));
				} else {
					throw new RuntimeException(format("Query-Based view generation failed for file: %s; received unexpected response from server: %s", qbvFile.getFile().getAbsolutePath(), handleString.get()));
				}
			} else {
				throw new RuntimeException(format("Query-Based View generation failed for file: %s; ensure your Optic script includes a call to generate a view; received unexpected response from server: %s", qbvFile.getFile().getAbsolutePath(), handleString.get()));
			}
		}
	}

	private ServerEvaluationCall getServerEvaluationCall(DocumentFile qbvFile, DatabaseClient contentClient) {
		String fileContent;
		try {
			fileContent = new String(FileCopyUtils.copyToByteArray(qbvFile.getFile()));
		} catch (IOException e) {
			throw new RuntimeException(format("Unable to generate Query-Based View; could not read from file %s; cause: %s", qbvFile.getFile().getAbsolutePath(), e.getMessage()));
		}
		return FilenameUtil.isXqueryFile(qbvFile.getFile().getName()) ?
			contentClient.newServerEval().xquery(fileContent) :
			contentClient.newServerEval().javascript(fileContent);
	}
}
