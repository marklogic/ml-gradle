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
import com.marklogic.client.ext.helper.LoggingObject;
import com.marklogic.client.extra.jdom.JDOMHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class QbvDocumentFileProcessor extends LoggingObject implements DocumentFileProcessor {

	public static final String QBV_COLLECTION = "http://marklogic.com/xdmp/qbv";
	private static final String QBV_XML_PLAN_NAMESPACE = "http://marklogic.com/plan";
	private static final String QBV_XML_ROOT_ELEMENT = "query-based-view";
	private static final String JAVASCRIPT_EVAL_TEMPLATE = "declareUpdate(); xdmp.invokeFunction(function() {'use strict'; const op = require('/MarkLogic/optic'); return %s }, {database: xdmp.database('%s')})";
	private static final String XQUERY_EVAL_TEMPLATE = "xquery version \"1.0-ml\"; import module namespace op=\"http://marklogic.com/optic\" at \"/MarkLogic/optic.xqy\"; xdmp:invoke-function(function() {%s},<options xmlns=\"xdmp:eval\"><database>{xdmp:database('%s')}</database></options>)";

	final private DatabaseClient databaseClient;
	final private String qbvGeneratorDatabaseName;
	final protected List<DocumentFile> qbvFiles = new ArrayList<>();
	final private XMLDocumentManager docMgr;


	/**
	 * @param databaseClient - a MarkLogic DatabaseClient object for the Schemas database
	 */
	public QbvDocumentFileProcessor(DatabaseClient databaseClient, String qbvGeneratorDatabaseName) {
		this.databaseClient = databaseClient;
		this.qbvGeneratorDatabaseName = qbvGeneratorDatabaseName;
		this.docMgr = databaseClient.newXMLDocumentManager();
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
		if (uri != null && uri.startsWith("/qbv")) {
			String extension = documentFile.getFileExtension();
			if (extension != null) {
				extension = extension.toLowerCase();
			}
			return
				"sjs".equals(extension)
					|| "js".equals(extension)
					|| "xqy".equals(extension)
					|| "xq".equals(extension);
		} else return false;
	}

	public void processQbvFiles() {
		qbvFiles.forEach(this::processQbvFile);
		qbvFiles.clear();
	}

	private void processQbvFile(DocumentFile qbvFile) {
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
						docMgr.write(uri, qbvFile.getDocumentMetadata(), new JDOMHandle(xmlDocument));
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
		String extension = qbvFile.getFileExtension();
		if (extension != null) {
			extension = extension.toLowerCase();
		}
		if (("xqy".equals(extension)) || ("xq".equals(extension))) {
			return buildXqueryCall(fileContent);
		} else {
			return buildJavascriptCall(fileContent);
		}
	}

	private ServerEvaluationCall buildJavascriptCall(String fileContent) {
		String script = format(JAVASCRIPT_EVAL_TEMPLATE, fileContent, qbvGeneratorDatabaseName);
		return databaseClient.newServerEval().javascript(script);
	}

	private ServerEvaluationCall buildXqueryCall(String fileContent) {
		String script = format(XQUERY_EVAL_TEMPLATE, fileContent, qbvGeneratorDatabaseName);
		return databaseClient.newServerEval().xquery(script);
	}
}
