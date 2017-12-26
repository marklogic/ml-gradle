package com.marklogic.client.ext.datamovement.job;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.datamovement.ExportListener;
import com.marklogic.client.document.ServerTransform;
import com.marklogic.client.ext.datamovement.QueryBatcherJobTicket;
import com.marklogic.client.ext.datamovement.consumer.WriteToZipConsumer;

import java.io.File;

public class ExportToZipJob extends AbstractQueryBatcherJob {

	private File exportFile;
	private WriteToZipConsumer writeToZipConsumer;
	private ExportListener exportListener;

	public ExportToZipJob() {
		super();

		addRequiredJobProperty("exportPath", "The path of the zip file to which selected records are exported",
			value -> setExportFile(new File(value)));

		addJobProperty("flattenUri", "Whether or not record URIs are flattened before being used as zip entry names; defaults to false",
			value -> getWriteToZipConsumer().setFlattenUri(Boolean.parseBoolean(value)));

		addJobProperty("transform", "The name of a REST transform to apply to each record before it is written to the zip file",
			value -> getExportListener().withTransform(new ServerTransform(value)));

		addJobProperty("uriPrefix", "Prefix to prepend to each URI it is used as an entry name; applied after a URI is optionally flattened",
			value -> getWriteToZipConsumer().setUriPrefix(value));
	}

	public ExportToZipJob(File exportFile) {
		this();
		setExportFile(exportFile);
	}

	@Override
	public QueryBatcherJobTicket run(DatabaseClient databaseClient) {
		QueryBatcherJobTicket ticket = super.run(databaseClient);

		if (writeToZipConsumer != null) {
			writeToZipConsumer.close();
		}

		return ticket;
	}

	public void setExportFile(File exportFile) {
		this.exportFile = exportFile;
		this.exportFile = exportFile;
		if (this.exportFile.getParentFile() != null) {
			this.exportFile.getParentFile().mkdirs();
		}

		this.writeToZipConsumer = new WriteToZipConsumer(exportFile);

		this.exportListener = new ExportListener();
		this.exportListener.onDocumentReady(writeToZipConsumer);
		this.addUrisReadyListener(this.exportListener);
	}

	@Override
	protected String getJobDescription() {
		return "Exporting documents " + getQueryDescription() + " to file at: " + exportFile;
	}

	/**
	 * Allow client to fiddle with the ExportListener created by this class.
	 *
	 * @return
	 */
	public ExportListener getExportListener() {
		return exportListener;
	}

	/**
	 * Allow client to fiddle with the WriteToZipConsumer created by this class.
	 *
	 * @return
	 */
	public WriteToZipConsumer getWriteToZipConsumer() {
		return writeToZipConsumer;
	}

}
