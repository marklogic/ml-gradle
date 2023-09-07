/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.client.ext.schemasloader.impl;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.eval.ServerEvaluationCall;
import com.marklogic.client.ext.file.DocumentFile;
import com.marklogic.client.ext.file.DocumentFileProcessor;
import com.marklogic.client.ext.helper.FilenameUtil;
import com.marklogic.client.ext.helper.LoggingObject;
import com.marklogic.client.extra.jdom.JDOMHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @since 4.6.0
 */
class QbvDocumentFileProcessor extends LoggingObject implements DocumentFileProcessor {

	public static final String QBV_COLLECTION = "http://marklogic.com/xdmp/qbv";
	private static final String QBV_XML_PLAN_NAMESPACE = "http://marklogic.com/plan";
	private static final String QBV_XML_ROOT_ELEMENT = "query-based-view";

	final private DatabaseClient contentDatabaseClient;
	final private List<DocumentFile> qbvFiles = new ArrayList<>();
	final private XMLDocumentManager schemasDocumentManager;


	/**
	 * @param schemasDatabaseClient used to write the QBV XML document to the application's schemas database
	 * @param contentDatabaseClient used to generate the QBV based on a user-provided script
	 */
	QbvDocumentFileProcessor(DatabaseClient schemasDatabaseClient, DatabaseClient contentDatabaseClient) {
		this.schemasDocumentManager = schemasDatabaseClient.newXMLDocumentManager();
		this.contentDatabaseClient = contentDatabaseClient;
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
			logger.info(format("Generating Query-Based View for file: %s", qbvFile.getFile().getName()));
		}
		ServerEvaluationCall call = getServerEvaluationCall(qbvFile);
		if (call != null) {
			StringHandle handleString = new StringHandle();
			try {
				call.eval(handleString);
			} catch (Exception e) {
				throw new RuntimeException(format("Query-Based View generation failed for file: %s; cause: %s", qbvFile.getFile().getAbsolutePath(), e.getMessage()));
			}
			if (Format.XML.equals(handleString.getFormat())) {
				Document xmlDocument;
				try {
					xmlDocument = new SAXBuilder().build(new StringReader(handleString.get()));
				} catch (Exception e) {
					throw new RuntimeException(format("Query-Based View generation failed for file: %s; cause: %s", qbvFile.getFile().getAbsolutePath(), e.getMessage()));
				}
				Element root = xmlDocument.getRootElement();
				if (QBV_XML_ROOT_ELEMENT.equals(root.getName()) & (root.getNamespace() != null && root.getNamespace().getURI().equals(QBV_XML_PLAN_NAMESPACE))) {
					qbvFile.getDocumentMetadata().getCollections().add(QBV_COLLECTION);
					String uri = qbvFile.getUri() + ".xml";
					schemasDocumentManager.write(uri, qbvFile.getDocumentMetadata(), new JDOMHandle(xmlDocument));
				} else {
					throw new RuntimeException(format("Query-Based view generation failed for file: %s; received unexpected response from server: %s", qbvFile.getFile().getAbsolutePath(), handleString.get()));
				}
			} else {
				throw new RuntimeException(format("Query-Based View generation failed for file: %s; ensure your Optic script includes a call to generate a view; received unexpected response from server: %s", qbvFile.getFile().getAbsolutePath(), handleString.get()));
			}
		}
	}

	private ServerEvaluationCall getServerEvaluationCall(DocumentFile qbvFile) {
		String fileContent;
		try {
			fileContent = new String(FileCopyUtils.copyToByteArray(qbvFile.getFile()));
		} catch (IOException e) {
			throw new RuntimeException(format("Unable to generate Query-Based View; could not read from file %s; cause: %s", qbvFile.getFile().getAbsolutePath(), e.getMessage()));
		}
		return FilenameUtil.isXqueryFile(qbvFile.getFile().getName()) ?
			contentDatabaseClient.newServerEval().xquery(fileContent) :
			contentDatabaseClient.newServerEval().javascript(fileContent);
	}
}
