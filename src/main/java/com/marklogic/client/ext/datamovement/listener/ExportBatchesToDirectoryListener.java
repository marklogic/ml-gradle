package com.marklogic.client.ext.datamovement.listener;

import com.marklogic.client.datamovement.BatchFailureListener;
import com.marklogic.client.datamovement.ExportListener;
import com.marklogic.client.datamovement.ExportToWriterListener;
import com.marklogic.client.datamovement.QueryBatch;
import com.marklogic.client.document.DocumentManager;
import com.marklogic.client.document.ServerTransform;
import com.marklogic.client.io.Format;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Supports exporting each QueryBatch to a separate File in a given directory.
 * <p>
 * Extends ExportListener so that it can reuse the error handling in that class.
 * <p>
 * Reuses ExportToWriterListener. Many of the properties that can be set on this class are copied over to each
 * ExportToWriterListener, which handles writing each QueryBatch to a File via a FileWriter.
 */
public class ExportBatchesToDirectoryListener extends ExportListener {

	private static Logger logger = LoggerFactory.getLogger(ExportBatchesToDirectoryListener.class);

	private File exportDir;

	// Specific to this class - controls the name of each file that is written to
	private String fileExtension = "xml";
	private String filenamePrefix = "batch-";

	// Copied over to the ExportToWriterListener instances created by this class
	private boolean includeXmlOutputListener = true;
	private ServerTransform transform;
	private Format nonDocumentFormat;
	private boolean consistentSnapshot;
	private Set<DocumentManager.Metadata> categories = new HashSet();
	private String recordPrefix;
	private String recordSuffix;
	private String fileHeader;
	private String fileFooter;

	public ExportBatchesToDirectoryListener(File exportDir) {
		this.exportDir = exportDir;
		this.exportDir.mkdirs();
	}

	@Override
	public void processEvent(QueryBatch queryBatch) {
		try {
			writeDocuments(queryBatch);
		} catch (Throwable t) {
			for (BatchFailureListener<QueryBatch> queryBatchFailureListener : getBatchFailureListeners()) {
				try {
					queryBatchFailureListener.processFailure(queryBatch, t);
				} catch (Throwable t2) {
					logger.error("Exception thrown by an onFailure listener", t2);
				}
			}
		}
	}

	/**
	 * @param queryBatch
	 * @throws IOException
	 */
	protected void writeDocuments(QueryBatch queryBatch) throws IOException {
		File file = getFileForBatch(queryBatch, exportDir);

		FileWriter fileWriter = new FileWriter(file);
		try {
			if (fileHeader != null) {
				fileWriter.write(fileHeader);
			}

			ExportToWriterListener listener = new ExportToWriterListener(fileWriter);
			prepareExportToWriterListener(listener);
			listener.processEvent(queryBatch);

			if (fileFooter != null) {
				fileWriter.write(fileFooter);
			}
		} finally {
			fileWriter.close();
		}
	}

	/**
	 * Determine the File to write to for the given query batch.
	 *
	 * @param queryBatch
	 * @param exportDir
	 * @return
	 */
	protected File getFileForBatch(QueryBatch queryBatch, File exportDir) {
		String filename = queryBatch.getJobBatchNumber() + "." + fileExtension;
		if (filenamePrefix != null) {
			filename = filenamePrefix + filename;
		}
		return new File(exportDir, filename);
	}

	/**
	 * Copies all of the applicable properties to the listener that have been set on this class.
	 *
	 * @param listener
	 */
	protected void prepareExportToWriterListener(ExportToWriterListener listener) {
		if (includeXmlOutputListener) {
			listener.onGenerateOutput(new XmlOutputListener());
		}
		if (consistentSnapshot) {
			listener.withConsistentSnapshot();
		}
		if (categories != null) {
			for (DocumentManager.Metadata category : categories) {
				listener.withMetadataCategory(category);
			}
		}
		if (nonDocumentFormat != null) {
			listener.withNonDocumentFormat(nonDocumentFormat);
		}
		if (transform != null) {
			listener.withTransform(transform);
		}
		if (recordPrefix != null) {
			listener.withRecordPrefix(recordPrefix);
		}
		if (recordSuffix != null) {
			listener.withRecordSuffix(recordSuffix);
		}
	}

	public ExportBatchesToDirectoryListener withFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
		return this;
	}

	public ExportBatchesToDirectoryListener withConsistentSnapshot() {
		this.consistentSnapshot = true;
		return this;
	}

	public ExportBatchesToDirectoryListener withMetadataCategory(DocumentManager.Metadata category) {
		this.categories.add(category);
		return this;
	}

	public ExportBatchesToDirectoryListener withNonDocumentFormat(Format nonDocumentFormat) {
		this.nonDocumentFormat = nonDocumentFormat;
		return this;
	}

	public ExportBatchesToDirectoryListener withTransform(ServerTransform transform) {
		this.transform = transform;
		return this;
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

	public void setIncludeXmlOutputListener(boolean includeXmlOutputListener) {
		this.includeXmlOutputListener = includeXmlOutputListener;
	}

	public ExportBatchesToDirectoryListener withFilenamePrefix(String filenamePrefix) {
		this.filenamePrefix = filenamePrefix;
		return this;
	}
}
