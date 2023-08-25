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

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.eval.ServerEvaluationCall;
import com.marklogic.client.ext.file.DocumentFile;
import com.marklogic.client.ext.file.DocumentFileProcessor;
import com.marklogic.client.ext.helper.FilenameUtil;
import com.marklogic.client.ext.helper.LoggingObject;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.IOException;

public class TdeDocumentFileProcessor extends LoggingObject implements DocumentFileProcessor {

	private DatabaseClient schemasDatabaseClient;
	private String tdeValidationDatabase;
	private Boolean templateBatchInsertSupported;

	/**
	 * Use this constructor when you don't want any TDE validation to occur.
	 */
	public TdeDocumentFileProcessor() {
	}

	/**
	 * Use this constructor when you want to validate a TDE template before writing it to MarkLogic.
	 *
	 * @param schemasDatabaseClient database client for the application's schemas database
	 * @param tdeValidationDatabase the database to run a script against for validating a TDE
	 */
	public TdeDocumentFileProcessor(DatabaseClient schemasDatabaseClient, String tdeValidationDatabase) {
		this.schemasDatabaseClient = schemasDatabaseClient;
		this.tdeValidationDatabase = tdeValidationDatabase;
	}

	@Override
	public DocumentFile processDocumentFile(DocumentFile documentFile) {
		String uri = documentFile.getUri();
		String filename = documentFile.getFile() != null ? documentFile.getFile().getName() : null;
		boolean isTdeUri = (uri != null && uri.startsWith("/tde"));
		boolean isJsonTde = (isTdeUri && FilenameUtil.endsWithExtension(filename, ".json"))
			|| FilenameUtil.endsWithExtension(filename, ".tdej");
		boolean isXmlTde = (isTdeUri && FilenameUtil.endsWithExtension(filename, ".xml"))
			|| FilenameUtil.endsWithExtension(filename, ".tdex");

		// We have a test suggesting that a TDE may not be JSON or XML; that doesn't seem likely, but it also does not
		// appear to cause any issues.
		if (isTdeUri || isJsonTde || isXmlTde) {
			documentFile.getDocumentMetadata().withCollections(TdeUtil.TDE_COLLECTION);
			validateTdeTemplate(documentFile);
			if (isJsonTde) {
				documentFile.setFormat(Format.JSON);
			} else if (isXmlTde) {
				documentFile.setFormat(Format.XML);
			}
		}

		return documentFile;
	}

	private boolean isTemplateBatchInsertSupported() {
		if (this.templateBatchInsertSupported == null) {
			// Memoize this to avoid repeated calls; the result will always be the same unless the databaseClient is
			// modified, in which case templateBatchInsertSupported is set to null
			this.templateBatchInsertSupported = TdeUtil.templateBatchInsertSupported(schemasDatabaseClient);
		}
		return this.templateBatchInsertSupported;
	}

	protected void validateTdeTemplate(DocumentFile documentFile) {
		final File file = documentFile.getFile();
		if (schemasDatabaseClient == null) {
			logger.info("No DatabaseClient provided for TDE validation, so will not validate TDE templates");
		} else if (tdeValidationDatabase == null) {
			logger.info("No TDE validation database specified, so will not validate TDE templates");
		} else if (isTemplateBatchInsertSupported()) {
			logger.debug("Not performing TDE validation; it will be performed automatically via tde.templateBatchInsert");
		} else {
			String fileContent = null;
			try {
				fileContent = new String(FileCopyUtils.copyToByteArray(file));
			} catch (IOException e) {
				logger.warn("Could not read TDE template from file, will not validate; cause: " + e.getMessage());
			}
			if (fileContent != null) {
				ServerEvaluationCall call = null;
				if (Format.XML.equals(documentFile.getFormat())) {
					call = buildXqueryCall(documentFile, fileContent);
				} else if (Format.JSON.equals(documentFile.getFormat())) {
					call = buildJavascriptCall(documentFile, fileContent);
				} else {
					logger.info("Unrecognized file format, will not try to validate TDE template in file: " + file + "; format: " + documentFile.getFormat());
				}

				if (call != null) {
					ObjectNode node = (ObjectNode) call.eval(new JacksonHandle()).get();
					if (node.get("valid").asBoolean()) {
						logger.info("TDE template passed validation: " + file);
					} else {
						throw new RuntimeException(format("TDE template failed validation; file: %s; cause: %s", file, node.get("message").asText()));
					}
				}
			}
		}
	}

	protected ServerEvaluationCall buildJavascriptCall(DocumentFile documentFile, String fileContent) {
		StringBuilder script = new StringBuilder("var template; xdmp.invokeFunction(function() {var tde = require('/MarkLogic/tde.xqy');");
		script.append(format(
			"\nreturn tde.validate([xdmp.toJSON(template)], ['%s'])}, {database: xdmp.database('%s')})",
			documentFile.getUri(), tdeValidationDatabase
		));
		return schemasDatabaseClient.newServerEval().javascript(script.toString())
			.addVariable("template", new StringHandle(fileContent).withFormat(Format.JSON));
	}

	protected ServerEvaluationCall buildXqueryCall(DocumentFile documentFile, String fileContent) {
		StringBuilder script = new StringBuilder("import module namespace tde = 'http://marklogic.com/xdmp/tde' at '/MarkLogic/tde.xqy'; ");
		script.append("\ndeclare variable $template external; ");
		script.append("\nxdmp:invoke-function(function() { ");
		script.append(format(
			"\ntde:validate($template, '%s')}, <options xmlns='xdmp:eval'><database>{xdmp:database('%s')}</database></options>)",
			documentFile.getUri(), tdeValidationDatabase
		));
		return schemasDatabaseClient.newServerEval().xquery(script.toString()).addVariable("template", new StringHandle(fileContent).withFormat(Format.XML));
	}

	/**
	 * @return
	 * @deprecated since 4.6.0, will be removed in 5.0.0
	 */
	@Deprecated
	public DatabaseClient getDatabaseClient() {
		return schemasDatabaseClient;
	}

	/**
	 * @param databaseClient
	 * @deprecated since 4.6.0, will be removed in 5.0.0
	 */
	@Deprecated
	public void setDatabaseClient(DatabaseClient databaseClient) {
		this.schemasDatabaseClient = databaseClient;
		this.templateBatchInsertSupported = null;
	}

	/**
	 * @return
	 * @deprecated since 4.6.0, will be removed in 5.0.0
	 */
	@Deprecated
	public String getTdeValidationDatabase() {
		return tdeValidationDatabase;
	}

	/**
	 * @param tdeValidationDatabase
	 * @deprecated since 4.6.0, will be removed in 5.0.0
	 */
	@Deprecated
	public void setTdeValidationDatabase(String tdeValidationDatabase) {
		this.tdeValidationDatabase = tdeValidationDatabase;
	}
}
