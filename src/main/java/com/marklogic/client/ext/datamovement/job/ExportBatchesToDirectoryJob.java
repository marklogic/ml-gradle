package com.marklogic.client.ext.datamovement.job;

import com.marklogic.client.document.ServerTransform;
import com.marklogic.client.ext.datamovement.listener.ExportBatchesToDirectoryListener;

import java.io.File;

public class ExportBatchesToDirectoryJob extends AbstractQueryBatcherJob {

	private ExportBatchesToDirectoryListener exportBatchesToDirectoryListener;
	private File exportDir;

	public ExportBatchesToDirectoryJob() {
		super();

		// Need to process this property first so that the listener isn't null
		addRequiredJobProperty("exportPath", "Directory path to which each batch should be written as a file",
			value -> setExportDir(new File(value)));

		addJobProperty("fileHeader", "Content written to the start of each file",
			value -> getExportListener().withFileHeader(value));

		addJobProperty("fileFooter", "Content written to the end of each file",
			value -> getExportListener().withFileFooter(value));

		addJobProperty("filenamePrefix", "Prefix written to the beginning of the filename of each file; defaults to batch-",
			value -> getExportListener().withFilenamePrefix(value));

		addJobProperty("filenameExtension", "Filename extension for each file; defaults to .zip",
			value -> getExportListener().withFilenameExtension(value));

		addJobProperty("recordPrefix", "Optional content to be written before each record is written",
			value -> getExportListener().withRecordPrefix(value));

		addJobProperty("recordSuffix", "Optional content to be written after each record is written",
			value -> getExportListener().withRecordSuffix(value));

		addJobProperty("transform", "Optional REST transform to apply to each record before it is written",
			value -> getExportListener().withTransform(new ServerTransform(value)));
	}

	public ExportBatchesToDirectoryJob(File exportDir) {
		this();
		setExportDir(exportDir);
	}

	@Override
	protected String getJobDescription() {
		return "Exporting batches of documents " + getQueryDescription() + " to files at: " + exportDir;
	}

	public ExportBatchesToDirectoryListener getExportListener() {
		return exportBatchesToDirectoryListener;
	}

	public void setExportDir(File exportDir) {
		this.exportDir = exportDir;
		exportBatchesToDirectoryListener = new ExportBatchesToDirectoryListener(exportDir);
		addUrisReadyListener(exportBatchesToDirectoryListener);
	}

	public File getExportDir() {
		return exportDir;
	}
}
