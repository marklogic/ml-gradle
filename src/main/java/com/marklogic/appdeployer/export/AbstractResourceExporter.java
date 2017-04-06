package com.marklogic.appdeployer.export;

import com.marklogic.client.helper.LoggingObject;
import com.marklogic.mgmt.ManageClient;

/**
 * Base class that provides a few conveniences for implementing ResourceExporter.
 *
 * TODO Should be able to add TaskExecutor support to allow for parallelizing requests to the Management API to
 * export resources.
 */
public abstract class AbstractResourceExporter extends LoggingObject implements ResourceExporter {

	private ManageClient manageClient;
	private String format = "xml";

	protected AbstractResourceExporter(ManageClient manageClient) {
		this.manageClient = manageClient;
	}

	protected boolean isFormatXml() {
		return "xml".equalsIgnoreCase(format);
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public ManageClient getManageClient() {
		return manageClient;
	}
}
