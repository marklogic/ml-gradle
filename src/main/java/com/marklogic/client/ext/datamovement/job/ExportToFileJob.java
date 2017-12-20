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
	private String filePrefix;
	private String fileSuffix;
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
			throw new RuntimeException("Unable to open FileWriter on file: " + exportFile.getAbsolutePath() + "; cause: " + ie.getMessage(), ie);
		}

		this.exportToWriterListener = new ExportToWriterListener(fileWriter);
		this.addUrisReadyListener(exportToWriterListener);
	}

	@Override
	public QueryBatcherJobTicket run(DatabaseClient databaseClient) {
		QueryBatcherJobTicket ticket = super.run(databaseClient);

		if (ticket.getQueryBatcher().isStopped()) {
			try {
				if (fileSuffix != null) {
					fileWriter.write(fileSuffix);
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
			if (filePrefix != null) {
				fileWriter.write(filePrefix);
				fileWriter.write("\n");
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected String getJobDescription() {
		return "Exporting documents " + getQueryDescription() + " to file at: " + exportFile.getAbsolutePath();
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

	public void setFilePrefix(String filePrefix) {
		this.filePrefix = filePrefix;
	}

	public void setFileSuffix(String fileSuffix) {
		this.fileSuffix = fileSuffix;
	}

	public void setIncludeXmlOutputListener(boolean includeXmlOutputListener) {
		this.includeXmlOutputListener = includeXmlOutputListener;
	}
}
