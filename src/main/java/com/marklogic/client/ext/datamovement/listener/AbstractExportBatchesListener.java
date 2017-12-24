package com.marklogic.client.ext.datamovement.listener;

import com.marklogic.client.datamovement.BatchFailureListener;
import com.marklogic.client.datamovement.ExportListener;
import com.marklogic.client.datamovement.QueryBatch;
import com.marklogic.client.document.DocumentManager;
import com.marklogic.client.document.ServerTransform;
import com.marklogic.client.io.Format;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * Abstract class for implementing listeners that export batches of documents. Extends ExportListener to benefit from
 * some of the initialization performed by that class. Follows the same "with" convention for setting properties.
 */
public abstract class AbstractExportBatchesListener extends ExportListener {

	private String fileExtension = "";
	private String filenamePrefix = "batch-";

	private ServerTransform transform;
	private Format nonDocumentFormat;
	private boolean consistentSnapshot;
	private Set<DocumentManager.Metadata> categories = new HashSet();

	protected abstract void exportBatch(QueryBatch queryBatch);

	@Override
	public void processEvent(QueryBatch queryBatch) {
		try {
			exportBatch(queryBatch);
		} catch (Throwable t) {
			for (BatchFailureListener<QueryBatch> queryBatchFailureListener : getBatchFailureListeners()) {
				try {
					queryBatchFailureListener.processFailure(queryBatch, t);
				} catch (Throwable t2) {
					LoggerFactory.getLogger(getClass()).error("Exception thrown by an onFailure listener", t2);
				}
			}
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
		String filename = queryBatch.getJobBatchNumber() + fileExtension;
		if (filenamePrefix != null) {
			filename = filenamePrefix + filename;
		}
		return new File(exportDir, filename);
	}

	/**
	 * Prepares each ExportListener created by a subclass before it's used to process a QueryBatch. Subclasses are
	 * expected to call this with their own instance of ExportListener or a subclass of it.
	 *
	 * @param listener
	 */
	protected void prepareExportListener(ExportListener listener) {
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
	}

	public AbstractExportBatchesListener withFilenamePrefix(String filenamePrefix) {
		this.filenamePrefix = filenamePrefix;
		return this;
	}

	public AbstractExportBatchesListener withFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
		return this;
	}

	public AbstractExportBatchesListener withConsistentSnapshot() {
		this.consistentSnapshot = true;
		return this;
	}

	public AbstractExportBatchesListener withMetadataCategory(DocumentManager.Metadata category) {
		this.categories.add(category);
		return this;
	}

	public AbstractExportBatchesListener withNonDocumentFormat(Format nonDocumentFormat) {
		this.nonDocumentFormat = nonDocumentFormat;
		return this;
	}

	public AbstractExportBatchesListener withTransform(ServerTransform transform) {
		this.transform = transform;
		return this;
	}


}
