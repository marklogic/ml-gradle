package com.marklogic.client.ext.datamovement.job;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.datamovement.ExportListener;
import com.marklogic.client.ext.datamovement.QueryBatcherJobTicket;
import com.marklogic.client.ext.datamovement.consumer.WriteToZipConsumer;

import java.io.File;

public class ExportToZipJob extends AbstractQueryBatcherJob {

	private File exportFile;
	private WriteToZipConsumer writeToZipConsumer;
	private ExportListener exportListener;

	public ExportToZipJob(File exportFile) {
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
	public QueryBatcherJobTicket run(DatabaseClient databaseClient) {
		QueryBatcherJobTicket ticket = super.run(databaseClient);

		if (writeToZipConsumer != null) {
			writeToZipConsumer.close();
		}

		return ticket;
	}

	@Override
	protected String getJobDescription() {
		return "Exporting documents " + getQueryDescription() + " to file at: " + exportFile.getAbsolutePath();
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
