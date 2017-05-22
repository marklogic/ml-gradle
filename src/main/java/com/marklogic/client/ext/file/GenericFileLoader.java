package com.marklogic.client.ext.file;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.batch.BatchWriter;
import com.marklogic.client.batch.RestBatchWriter;
import com.marklogic.client.ext.tokenreplacer.TokenReplacer;
import com.marklogic.client.helper.LoggingObject;

import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

public class GenericFileLoader extends LoggingObject {

	private DocumentFileReader documentFileReader;
	private BatchWriter batchWriter;
	private boolean waitForCompletion = true;

	// These are passed on to the DefaultDocumentFileReader that is created if one isn't set
	private List<FileFilter> fileFilters;
	private List<DocumentFileProcessor> documentFileProcessors;

	// Properties that affect the DocumentFileProcessors that are created for the DefaultDocumentFileReader
	private String permissions;
	private String[] collections;
	private TokenReplacer tokenReplacer;

	public GenericFileLoader(DatabaseClient client) {
		RestBatchWriter restBatchWriter = new RestBatchWriter(client);
		restBatchWriter.setReleaseDatabaseClients(false);
		this.batchWriter = restBatchWriter;
	}

	public GenericFileLoader(BatchWriter batchWriter, DocumentFileReader documentFileReader) {
		this.batchWriter = batchWriter;
		this.documentFileReader = documentFileReader;
	}

	/**
	 * Load files from each of the given paths, using the underlying DocumentFileReader.
	 * <p>
	 * If a DocumentFileReader has not been set yet, then one will be constructed before any files are read.
	 *
	 * @param paths
	 * @return
	 */
	public List<DocumentFile> loadFiles(String... paths) {
		batchWriter.initialize();
		if (documentFileReader == null) {
			documentFileReader = buildDocumentFileReader();
		}

		List<DocumentFile> documentFiles = documentFileReader.readDocumentFiles(paths);
		if (documentFiles != null && !documentFiles.isEmpty()) {
			if (logger.isInfoEnabled()) {
				logger.info(format("Writing %d files", documentFiles.size()));
			}
			batchWriter.write(documentFiles);
			if (waitForCompletion) {
				batchWriter.waitForCompletion();
			}
		}
		return documentFiles;
	}

	/**
	 * If no DocumentFileReader is set, this will construct a DefaultDocumentFileReader using several of the properties
	 * of this class.
	 *
	 * @return
	 */
	protected DocumentFileReader buildDocumentFileReader() {
		DefaultDocumentFileReader reader = new DefaultDocumentFileReader();

		for (DocumentFileProcessor processor : buildDocumentFileProcessors()) {
			reader.addDocumentFileProcessor(processor);
		}

		if (fileFilters != null) {
			for (FileFilter filter : fileFilters) {
				reader.addFileFilter(filter);
			}
		}

		return reader;
	}

	protected List<DocumentFileProcessor> buildDocumentFileProcessors() {
		List<DocumentFileProcessor> processors = new ArrayList<>();
		if (permissions != null) {
			processors.add(new PermissionsDocumentFileProcessor(permissions));
		}
		if (collections != null) {
			processors.add(new CollectionsDocumentFileProcessor(collections));
		}
		if (tokenReplacer != null) {
			processors.add(new TokenReplacerDocumentFileProcessor(tokenReplacer));
		}

		if (documentFileProcessors != null) {
			for (DocumentFileProcessor dfp : documentFileProcessors) {
				processors.add(dfp);
			}
		}

		return processors;
	}

	public void setWaitForCompletion(boolean waitForCompletion) {
		this.waitForCompletion = waitForCompletion;
	}

	public void setDocumentFileReader(DocumentFileReader documentFileReader) {
		this.documentFileReader = documentFileReader;
	}

	public void setPermissions(String permissions) {
		this.permissions = permissions;
	}

	public void setCollections(String... collections) {
		this.collections = collections;
	}

	public void setTokenReplacer(TokenReplacer tokenReplacer) {
		this.tokenReplacer = tokenReplacer;
	}

	public void addFileFilter(FileFilter fileFilter) {
		if (fileFilters == null) {
			fileFilters = new ArrayList<>();
		}
		fileFilters.add(fileFilter);
	}

	public void setFileFilters(List<FileFilter> fileFilters) {
		this.fileFilters = fileFilters;
	}

	public void addDocumentFileProcessor(DocumentFileProcessor processor) {
		if (documentFileProcessors == null) {
			documentFileProcessors = new ArrayList<>();
		}
		documentFileProcessors.add(processor);
	}

	public void setDocumentFileProcessors(List<DocumentFileProcessor> documentFileProcessors) {
		this.documentFileProcessors = documentFileProcessors;
	}
}
