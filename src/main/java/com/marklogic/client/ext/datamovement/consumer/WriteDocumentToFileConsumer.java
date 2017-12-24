package com.marklogic.client.ext.datamovement.consumer;

import com.marklogic.client.document.DocumentRecord;
import com.marklogic.client.ext.helper.LoggingObject;
import com.marklogic.client.io.InputStreamHandle;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.function.Consumer;

/**
 * Consumer implementation that is intended to be used with DMSDK's ExportListener. Writes each document to a File based
 * on the directory passed to this class's constructor plus the document's URI.
 */
public class WriteDocumentToFileConsumer extends LoggingObject implements Consumer<DocumentRecord> {

	private File baseDir;
	private boolean logErrors = true;

	public WriteDocumentToFileConsumer(File baseDir) {
		this.baseDir = baseDir;
		this.baseDir.mkdirs();
	}

	@Override
	public void accept(DocumentRecord documentRecord) {
		String uri = documentRecord.getUri();
		File outputFile = getOutputFile(documentRecord);
		if (logger.isDebugEnabled()) {
			logger.debug("Writing document with URI " + uri + " to file: " + outputFile);
		}
		try {
			writeDocumentToFile(documentRecord, outputFile);
		} catch (IOException e) {
			String message = "Unable to write document to file; URI: " + uri + "; file: " + outputFile;
			if (logErrors) {
				logger.warn(message, e);
			} else {
				throw new RuntimeException(message, e);
			}
		}
	}

	protected File getOutputFile(DocumentRecord documentRecord) {
		return new File(baseDir, documentRecord.getUri());
	}

	protected void writeDocumentToFile(DocumentRecord documentRecord, File file) throws IOException {
		file.getParentFile().mkdirs();
		FileOutputStream fos = new FileOutputStream(file);
		try {
			InputStreamHandle handle = documentRecord.getContent(new InputStreamHandle());
			FileCopyUtils.copy(handle.get(), fos);
		} finally {
			fos.close();
		}
	}

	protected File getBaseDir() {
		return baseDir;
	}

	protected boolean isLogErrors() {
		return logErrors;
	}

	public void setLogErrors(boolean logErrors) {
		this.logErrors = logErrors;
	}
}
