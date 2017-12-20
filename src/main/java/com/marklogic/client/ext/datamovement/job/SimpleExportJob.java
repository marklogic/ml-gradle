package com.marklogic.client.ext.datamovement.job;

import com.marklogic.client.datamovement.ExportListener;
import com.marklogic.client.document.DocumentRecord;

import java.util.function.Consumer;

/**
 * Simple job that uses ExportListener with zero or more Consumer objects.
 */
public class SimpleExportJob extends AbstractQueryBatcherJob {

	private ExportListener exportListener;

	public SimpleExportJob(Consumer<DocumentRecord>... consumers) {
		exportListener = new ExportListener();
		for (Consumer<DocumentRecord> consumer : consumers) {
			exportListener.onDocumentReady(consumer);
		}
		this.addUrisReadyListener(exportListener);
	}

	@Override
	protected String getJobDescription() {
		return "Exporting documents " + getQueryDescription();
	}

	/**
	 * Allow for a client to fiddle with the ExportListener created by this class.
	 *
	 * @return
	 */
	public ExportListener getExportListener() {
		return exportListener;
	}
}
