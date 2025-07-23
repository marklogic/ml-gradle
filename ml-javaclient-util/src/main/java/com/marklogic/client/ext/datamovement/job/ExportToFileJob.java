/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.ext.datamovement.job;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.datamovement.ExportToWriterListener;
import com.marklogic.client.datamovement.QueryBatcher;
import com.marklogic.client.ext.datamovement.QueryBatcherJobTicket;
import com.marklogic.client.ext.datamovement.listener.XmlOutputListener;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Job for exporting all documents matching a query to a single file.
 * <p>
 * The 1.1 release added <code>setOmitLastRecordSuffix</code>, which can be used to (among other things) write all
 * JSON documents to an array via a record suffix of ",", but the comma after the last document will be removed so that
 * a valid array is written to the file.
 */
public class ExportToFileJob extends AbstractQueryBatcherJob {

	private File exportFile;
	private String fileHeader;
	private String fileFooter;
	private FileWriter fileWriter;
	private ExportToWriterListener exportToWriterListener;
	private boolean includeXmlOutputListener = true;

	private String recordSuffix;
	private boolean omitLastRecordSuffix = false;

	public ExportToFileJob() {
		super();

		addRequiredJobProperty("exportPath", "The path of the file to which selected records are exported",
			value -> setExportFile(new File(value)));

		addJobProperty("fileHeader", "Optional content that should be written to the start of each file",
			value -> setFileHeader(value));

		addJobProperty("fileFooter", "Optional content that should be written to the end of each file",
			value -> setFileFooter(value));

		addJobProperty("omitLastRecordSuffix", "If a recordSuffix is specified, and this is set to true, then the " +
				"record suffix will not be written after the last record is written to the file (the file footer will still be written)",
			value -> setOmitLastRecordSuffix(Boolean.parseBoolean(value)));

		addJobProperty("recordPrefix", "Optional content to be written before each record is written",
			value -> getExportListener().withRecordPrefix(value));

		addJobProperty("recordSuffix", "Optional content to be written after each record is written",
			value -> setRecordSuffix(value));

		addTransformJobProperty((value, transform) -> getExportListener().withTransform(transform));
	}

	public ExportToFileJob(File exportFile) {
		this();
		setExportFile(exportFile);
	}

	@Override
	protected String getJobDescription() {
		return "Exporting documents " + getQueryDescription() + " to file at: " + exportFile;
	}

	/**
	 * If omitLastRecordSuffix is set to true, be sure to use this to set the recordSuffix instead of
	 * getExportListener().withRecordSuffix.
	 *
	 * @param recordSuffix
	 */
	public void setRecordSuffix(String recordSuffix) {
		this.recordSuffix = recordSuffix;
		if (this.exportToWriterListener != null) {
			this.exportToWriterListener.withRecordSuffix(recordSuffix);
		}
	}

	/**
	 * Initializes this class's FileWriter and ExportToWriterListener.
	 *
	 * @param exportFile
	 */
	public void setExportFile(File exportFile) {
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
		if (this.recordSuffix != null) {
			this.exportToWriterListener.withRecordSuffix(recordSuffix);
		}
		this.addUrisReadyListener(exportToWriterListener);
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
	public QueryBatcherJobTicket run(DatabaseClient databaseClient) {
		QueryBatcherJobTicket ticket = super.run(databaseClient);

		if (ticket.getQueryBatcher().isStopped()) {
			if (omitLastRecordSuffix && this.recordSuffix != null) {
				removeLastRecordSuffixAndWriteFileFooter(fileWriter, recordSuffix, fileFooter);
			} else {
				writeFileFooter(fileWriter, fileFooter);
			}
		}

		return ticket;
	}

	/**
	 * If the last record suffix should be omitted, then the FileWriter is closed and a RandomAccessFile is used to seek
	 * to the end of the file minus the length of the record suffix. It is then overwritten with the file footer, or if
	 * a file footer is not, then white space is added which matches the length of the record suffix.
	 *
	 * @param fileWriter
	 * @param recordSuffix
	 * @param fileFooter
	 */
	protected void removeLastRecordSuffixAndWriteFileFooter(FileWriter fileWriter, String recordSuffix, String fileFooter) {
		closeFileWriter(fileWriter);
		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(this.exportFile, "rw");
			int len = recordSuffix.length();
			raf.seek(raf.length() - len);
			if (fileFooter != null) {
				raf.write(fileFooter.getBytes());
			} else {
				for (int i = 0; i < len; i++) {
					raf.write(" ".getBytes());
				}
			}
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		} finally {
			try {
				if (raf != null) {
					raf.close();
				}
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		}
	}

	protected void writeFileFooter(FileWriter fileWriter, String fileFooter) {
		try {
			if (fileFooter != null) {
				fileWriter.write(fileFooter);
			}
		} catch (IOException ie) {
			throw new RuntimeException(ie);
		} finally {
			closeFileWriter(fileWriter);
		}
	}

	protected void closeFileWriter(FileWriter fileWriter) {
		try {
			fileWriter.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Allow client to fiddle with the FileWriter that's created by this class. Note that an exportFile must already
	 * have been set either via this class's constructor or setExportFile; otherwise, the FileWriter will be null.
	 *
	 * @return
	 */
	public FileWriter getFileWriter() {
		return fileWriter;
	}

	public File getExportFile() {
		return exportFile;
	}

	/**
	 * Allow client to fiddle with the ExportToWriterListener that's created by this class. Note that an exportFile must already
	 * have been set either via this class's constructor or setExportFile; otherwise, the FileWriter will be null.
	 *
	 * @return
	 */
	public ExportToWriterListener getExportListener() {
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

	public void setOmitLastRecordSuffix(boolean omitLastRecordSuffix) {
		this.omitLastRecordSuffix = omitLastRecordSuffix;
	}
}
