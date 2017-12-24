package com.marklogic.client.ext.datamovement.job;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.datamovement.ExportToWriterListener;
import com.marklogic.client.datamovement.QueryBatcher;
import com.marklogic.client.ext.datamovement.QueryBatcherJobTicket;
import com.marklogic.client.ext.datamovement.listener.XmlOutputListener;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ExportToFileJob extends AbstractQueryBatcherJob {

	private File exportFile;
	private String fileHeader;
	private String fileFooter;
	private FileWriter fileWriter;
	private ExportToWriterListener exportToWriterListener;
	private boolean includeXmlOutputListener = true;

	public ExportToFileJob(File exportFile) {
		this.exportFile = exportFile;
		File parentFile = this.exportFile.getParentFile();
		if (parentFile != null) {
			parentFile.mkdirs();
		}
		try {
			this.fileWriter = new FileWriter(exportFile);
		} catch (IOException ie) {
			throw new RuntimeException("Unable to open FileWriter on file: " + exportFile + "; cause: " + ie.getMessage(), ie);
		}

		this.exportToWriterListener = new ExportToWriterListener(fileWriter);
		this.addUrisReadyListener(exportToWriterListener);
	}

	@Override
	public QueryBatcherJobTicket run(DatabaseClient databaseClient) {
		QueryBatcherJobTicket ticket = super.run(databaseClient);

		if (ticket.getQueryBatcher().isStopped()) {
			try {
				if (fileFooter != null) {
					fileWriter.write(fileFooter);
				}
			} catch (IOException ie) {
				throw new RuntimeException(ie);
			} finally {
				try {
					this.fileWriter.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}

		return ticket;
	}

	@Override
	protected void prepareQueryBatcher(QueryBatcher queryBatcher) {
		super.prepareQueryBatcher(queryBatcher);

		if (includeXmlOutputListener) {
			this.exportToWriterListener.onGenerateOutput(new XmlOutputListener());
		}

		try {
			if (fileHeader != null) {
				fileWriter.write(fileHeader);
				fileWriter.write("\n");
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected String getJobDescription() {
		return "Exporting documents " + getQueryDescription() + " to file at: " + exportFile;
	}

	/**
	 * Allow client to fiddle with the FileWriter that's created by this class.
	 *
	 * @return
	 */
	public FileWriter getFileWriter() {
		return fileWriter;
	}

	/**
	 * Allow client to fiddle with the ExportToWriterListener that's created by this class.
	 *
	 * @return
	 */
	public ExportToWriterListener getExportToWriterListener() {
		return exportToWriterListener;
	}

	public void setFileHeader(String fileHeader) {
		this.fileHeader = fileHeader;
	}

	public void setFileFooter(String fileFooter) {
		this.fileFooter = fileFooter;
	}

	public void setIncludeXmlOutputListener(boolean includeXmlOutputListener) {
		this.includeXmlOutputListener = includeXmlOutputListener;
	}
}
