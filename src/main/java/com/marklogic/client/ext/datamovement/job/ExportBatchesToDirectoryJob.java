package com.marklogic.client.ext.datamovement.job;

import com.marklogic.client.ext.datamovement.listener.ExportBatchesToDirectoryListener;

import java.io.File;

public class ExportBatchesToDirectoryJob extends AbstractQueryBatcherJob {

	private ExportBatchesToDirectoryListener exportBatchesToDirectoryListener;
	private File exportDir;

	public ExportBatchesToDirectoryJob(File exportDir) {
		exportBatchesToDirectoryListener = new ExportBatchesToDirectoryListener(exportDir);
		addUrisReadyListener(exportBatchesToDirectoryListener);
		this.exportDir = exportDir;
	}

	@Override
	protected String getJobDescription() {
		return "Exporting batches of documents " + getQueryDescription() + " to files at: " + exportDir.getAbsolutePath();
	}

	public ExportBatchesToDirectoryListener getExportBatchesToDirectoryListener() {
		return exportBatchesToDirectoryListener;
	}
}
