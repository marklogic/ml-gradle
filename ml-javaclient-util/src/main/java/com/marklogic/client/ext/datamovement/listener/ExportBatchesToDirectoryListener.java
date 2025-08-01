/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.ext.datamovement.listener;

import com.marklogic.client.datamovement.ExportToWriterListener;
import com.marklogic.client.datamovement.QueryBatch;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Supports exporting each QueryBatch to a separate File in a given directory.
 * <p>
 * Extends ExportListener so that it can reuse the error handling in that class.
 * <p>
 * Reuses ExportToWriterListener. Many of the properties that can be set on this class are copied over to each
 * ExportToWriterListener, which handles writing each QueryBatch to a File via a FileWriter.
 */
public class ExportBatchesToDirectoryListener extends AbstractExportBatchesListener {

	private File exportDir;

	// Copied over to the ExportToWriterListener instances created by this class
	private boolean includeXmlOutputListener = true;
	private String recordPrefix;
	private String recordSuffix;
	private String fileHeader;
	private String fileFooter;

	public ExportBatchesToDirectoryListener(File exportDir) {
		withFilenameExtension(".xml");
		this.exportDir = exportDir;
		this.exportDir.mkdirs();
	}

	/**
	 * Uses a FileWriter to write the batch to a single file.
	 *
	 * @param queryBatch
	 */
	protected void exportBatch(QueryBatch queryBatch) {
		File file = getFileForBatch(queryBatch, exportDir);

		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(file);
			if (fileHeader != null) {
				fileWriter.write(fileHeader);
			}

			ExportToWriterListener listener = new ExportToWriterListener(fileWriter);
			prepareExportToWriterListener(listener);
			listener.processEvent(queryBatch);

			if (fileFooter != null) {
				fileWriter.write(fileFooter);
			}
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		} finally {
			if (fileWriter != null) {
				try {
					fileWriter.close();
				} catch (IOException e) {
					LoggerFactory.getLogger(getClass()).warn("Unable to close FileWriter: " + e.getMessage(), e);
				}
			}
		}
	}


	/**
	 * Copies all of the applicable properties to the listener that have been set on this class.
	 *
	 * @param listener
	 */
	protected void prepareExportToWriterListener(ExportToWriterListener listener) {
		super.prepareExportListener(listener);

		if (includeXmlOutputListener) {
			listener.onGenerateOutput(new XmlOutputListener());
		}
		if (recordPrefix != null) {
			listener.withRecordPrefix(recordPrefix);
		}
		if (recordSuffix != null) {
			listener.withRecordSuffix(recordSuffix);
		}
	}

	public ExportBatchesToDirectoryListener withRecordPrefix(String recordPrefix) {
		this.recordPrefix = recordPrefix;
		return this;
	}

	public ExportBatchesToDirectoryListener withRecordSuffix(String recordSuffix) {
		this.recordSuffix = recordSuffix;
		return this;
	}

	public ExportBatchesToDirectoryListener withFileHeader(String fileHeader) {
		this.fileHeader = fileHeader;
		return this;
	}

	public ExportBatchesToDirectoryListener withFileFooter(String fileFooter) {
		this.fileFooter = fileFooter;
		return this;
	}

	public ExportBatchesToDirectoryListener withXmlOutputListener(boolean includeXmlOutputListener) {
		this.includeXmlOutputListener = includeXmlOutputListener;
		return this;
	}
}
