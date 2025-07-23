/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.ext.datamovement.job;

import com.marklogic.client.ext.datamovement.listener.ExportBatchesToZipsListener;

import java.io.File;

public class ExportBatchesToZipsJob extends AbstractQueryBatcherJob {

	private File exportDir;
	private ExportBatchesToZipsListener exportBatchesToZipsListener;

	public ExportBatchesToZipsJob() {
		super();

		addRequiredJobProperty("exportPath", "Directory path to which each batch should be written as a zip",
			value -> setExportDir(new File(value)));

		addJobProperty("filenamePrefix", "Prefix written to the beginning of the filename of each file; defaults to batch-",
			value -> getExportListener().withFilenamePrefix(value));

		addJobProperty("filenameExtension", "Filename extension for each file; defaults to .zip",
			value -> getExportListener().withFilenameExtension(value));

		addJobProperty("flattenUri", "Whether or not record URIs are flattened before being used as zip entry names; defaults to false",
			value -> getExportListener().withFlattenUri(Boolean.parseBoolean(value)));

		addTransformJobProperty((value, transform) -> getExportListener().withTransform(transform));

		addJobProperty("uriPrefix", "Prefix to prepend to each URI it is used as an entry name; applied after a URI is optionally flattened",
			value -> getExportListener().withUriPrefix(value));

	}

	public ExportBatchesToZipsJob(File exportDir) {
		this();
		setExportDir(exportDir);
	}

	@Override
	protected String getJobDescription() {
		return "Exporting batches of documents " + getQueryDescription() + " to files at: " + exportDir;
	}

	public ExportBatchesToZipsListener getExportListener() {
		return exportBatchesToZipsListener;
	}

	public void setExportDir(File exportDir) {
		this.exportDir = exportDir;
		this.exportBatchesToZipsListener = new ExportBatchesToZipsListener(exportDir);
		this.addUrisReadyListener(exportBatchesToZipsListener);
	}

	public File getExportDir() {
		return exportDir;
	}
}
