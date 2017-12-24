package com.marklogic.client.ext.datamovement.job;

import com.marklogic.client.ext.datamovement.listener.ExportBatchesToZipsListener;

import java.io.File;

public class ExportBatchesToZipsJob extends AbstractQueryBatcherJob {

	private File exportDir;
	private ExportBatchesToZipsListener exportBatchesToZipsListener;

	public ExportBatchesToZipsJob(File exportDir) {
		this.exportDir = exportDir;
		this.exportBatchesToZipsListener = new ExportBatchesToZipsListener(exportDir);
		this.addUrisReadyListener(exportBatchesToZipsListener);
	}

	@Override
	protected String getJobDescription() {
		return "Exporting batches of documents " + getQueryDescription() + " to files at: " + exportDir;
	}

	public ExportBatchesToZipsListener getExportBatchesToZipsListener() {
		return exportBatchesToZipsListener;
	}
}
