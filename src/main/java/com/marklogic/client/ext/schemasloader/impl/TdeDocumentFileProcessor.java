package com.marklogic.client.ext.schemasloader.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.eval.ServerEvaluationCall;
import com.marklogic.client.ext.file.DocumentFile;
import com.marklogic.client.ext.file.DocumentFileProcessor;
import com.marklogic.client.ext.helper.LoggingObject;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.IOException;

public class TdeDocumentFileProcessor extends LoggingObject implements DocumentFileProcessor {

	private DatabaseClient databaseClient;
	private String tdeValidationDatabase;

	/**
	 * Use this constructor when you don't want any TDE validation to occur.
	 */
	public TdeDocumentFileProcessor() {
	}

	/**
	 * Use this constructor when you want to validate a TDE template before writing it to MarkLogic.
	 *
	 * @param databaseClient
	 */
	public TdeDocumentFileProcessor(DatabaseClient databaseClient, String tdeValidationDatabase) {
		this.databaseClient = databaseClient;
		this.tdeValidationDatabase = tdeValidationDatabase;
	}

	@Override
	public DocumentFile processDocumentFile(DocumentFile documentFile) {
		String uri = documentFile.getUri();
		String extension = documentFile.getFileExtension();
		if (extension != null) {
			extension = extension.toLowerCase();
		}

		boolean isTdeTemplate = ("tdej".equals(extension) || "tdex".equals(extension)) || (uri != null && uri.startsWith("/tde"));
		if (isTdeTemplate) {
			documentFile.getDocumentMetadata().withCollections("http://marklogic.com/xdmp/tde");
		}

		if ("tdej".equals(extension) || "json".equals(extension)) {
			documentFile.setFormat(Format.JSON);
		} else if ("tdex".equals(extension) || "xml".equals(extension)) {
			documentFile.setFormat(Format.XML);
		}

		if (isTdeTemplate) {
			validateTdeTemplate(documentFile);
		}

		return documentFile;
	}

	protected void validateTdeTemplate(DocumentFile documentFile) {
		final File file = documentFile.getFile();
		if (databaseClient == null) {
			logger.info("No DatabaseClient provided for TDE validation, so will not validate TDE templates");
		} else if (tdeValidationDatabase == null) {
			logger.info("No TDE validation database specified, so will not validate TDE templates");
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
		return databaseClient.newServerEval().javascript(script.toString())
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
		return databaseClient.newServerEval().xquery(script.toString()).addVariable("template", new StringHandle(fileContent).withFormat(Format.XML));
	}

	public DatabaseClient getDatabaseClient() {
		return databaseClient;
	}

	public void setDatabaseClient(DatabaseClient databaseClient) {
		this.databaseClient = databaseClient;
	}

	public String getTdeValidationDatabase() {
		return tdeValidationDatabase;
	}

	public void setTdeValidationDatabase(String tdeValidationDatabase) {
		this.tdeValidationDatabase = tdeValidationDatabase;
	}
}
