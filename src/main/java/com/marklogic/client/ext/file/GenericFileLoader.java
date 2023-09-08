/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.client.ext.file;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.ext.batch.BatchWriter;
import com.marklogic.client.ext.batch.RestBatchWriter;
import com.marklogic.client.ext.helper.LoggingObject;
import com.marklogic.client.ext.tokenreplacer.TokenReplacer;

import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Generic implementation of FileLoader. Delegates to a DocumentFileReader for reading from a set of file paths, and
 * delegates to a BatchWriter for writing to MarkLogic (where that BatchWriter could use XCC, the REST API, or the
 * Data Movement SDK in ML9).
 * <p>
 * The batchSize property defaults to null, which means all files are written in one call via the BatchWriter. Setting
 * this means that the List of DocumentFile objects read from the DocumentFileReader will be written in batches, each
 * the size of the batchSize property, except for the final one that may be less than this size.
 */
public class GenericFileLoader extends LoggingObject implements FileLoader {

	private DocumentFileReader documentFileReader;
	private BatchWriter batchWriter;
	private boolean waitForCompletion = true;
	private boolean logFileUris = true;
	private Integer batchSize;

	// These are passed on to the DefaultDocumentFileReader that is created if one isn't set
	private List<FileFilter> fileFilters;
	private List<DocumentFileProcessor> documentFileProcessors;

	// Properties that affect the DocumentFileProcessors that are created for the DefaultDocumentFileReader
	private String permissions;
	private String[] collections;
	private TokenReplacer tokenReplacer;
	private String[] additionalBinaryExtensions;
	private boolean cascadeCollections;
	private boolean cascadePermissions;

	/**
	 * The given DatabaseClient is used to construct a BatchWriter that writes to MarkLogic via the REST API. The
	 * expectation is that the client will then either call setDocumentFileReader or will rely on the default one
	 * that's created by this class if one has not yet been set.
	 *
	 * @param client
	 */
	public GenericFileLoader(DatabaseClient client) {
		RestBatchWriter restBatchWriter = new RestBatchWriter(client);
		restBatchWriter.setReleaseDatabaseClients(false);
		this.batchWriter = restBatchWriter;
	}

	/**
	 * Clients should use this when they already have a BatchWriter ready to go.
	 *
	 * @param batchWriter
	 */
	public GenericFileLoader(BatchWriter batchWriter) {
		this.batchWriter = batchWriter;
	}

	/**
	 * Load files from each of the given paths, using the underlying DocumentFileReader.
	 * <p>
	 * If a DocumentFileReader has not been set yet, then one will be constructed before any files are read.
	 *
	 * @param paths
	 * @return list of files from the given paths
	 */
	public List<DocumentFile> loadFiles(String... paths) {
		List<DocumentFile> documentFiles = getDocumentFiles(paths);
		writeDocumentFiles(documentFiles);
		return documentFiles;
	}

	protected final List<DocumentFile> getDocumentFiles(String... paths) {
		initializeDocumentFileReader();
		return documentFileReader.readDocumentFiles(paths);
	}

	protected final void writeDocumentFiles(List<DocumentFile> documentFiles) {
		if (documentFiles != null && !documentFiles.isEmpty()) {
			batchWriter.initialize();
			writeBatchOfDocuments(documentFiles, 0);
			if (waitForCompletion) {
				batchWriter.waitForCompletion();
			}
		}
	}

	/**
	 * If batchSize is not set, then this method will load all the documents in one call to the BatchWriter. Otherwise,
	 * this will divide up the list of documentFiles into batches matching the value of batchSize, with the last batch
	 * possibly being less than batchSize.
	 *
	 * @param documentFiles
	 * @param startPosition
	 */
	protected void writeBatchOfDocuments(List<DocumentFile> documentFiles, final int startPosition) {
		final int documentFilesSize = documentFiles.size();
		if (startPosition >= documentFilesSize) {
			return;
		}

		if (batchSize != null && batchSize < 1) {
			batchSize = null;
		}

		// The "end" param to subList below is exclusive, so the highest valid value is the list size
		int endPosition = batchSize == null ? documentFilesSize : startPosition + batchSize;
		if (endPosition > documentFilesSize) {
			endPosition = documentFilesSize;
		}

		List<DocumentFile> batch = documentFiles.subList(startPosition, endPosition);
		if (!batch.isEmpty()) {
			final boolean infoEnabled = logger.isInfoEnabled();
			if (infoEnabled) {
				logger.info(format("Writing %d files", batch.size()));
			}
			List<DocumentWriteOperation> documentWriteOperations = batch.stream().map(file -> {
				if (logFileUris && infoEnabled) {
					final String uri = file.getUri();
					logger.info("Writing: " + uri != null ? uri : file.getTemporalDocumentURI());
				}
				return file.toDocumentWriteOperation();
			}).collect(Collectors.toList());
			batchWriter.write(documentWriteOperations);
		}

		if (endPosition < documentFilesSize) {
			writeBatchOfDocuments(documentFiles, endPosition);
		}
	}

	/**
	 * If no DocumentFileReader is set, this will construct a DefaultDocumentFileReader, which is then configured based
	 * on several properties of this class.
	 */
	public void initializeDocumentFileReader() {
		if (this.documentFileReader == null) {
			DefaultDocumentFileReader reader = new DefaultDocumentFileReader();

			if (fileFilters != null) {
				for (FileFilter filter : fileFilters) {
					reader.addFileFilter(filter);
				}
			}

			prepareAbstractDocumentFileReader(reader);
			this.documentFileReader = reader;
		}
	}

	/**
	 * This was initially part of building a DefaultDocumentFileReader. But in the event that a client sets a custom
	 * DocumentFileReader on this class that extends AbstractDocumentFileReader, it's useful to reuse this code on
	 * that custom DocumentFileReader. Thus, it's public.
	 *
	 * @param reader
	 */
	public void prepareAbstractDocumentFileReader(AbstractDocumentFileReader reader) {
		buildDocumentFileProcessors().forEach(processor -> reader.addDocumentFileProcessor(processor));

		reader.getDocumentFileProcessors().forEach(processor -> {
			if (tokenReplacer != null && processor instanceof SupportsTokenReplacer) {
				((SupportsTokenReplacer) processor).setTokenReplacer(tokenReplacer);
			}
			if (additionalBinaryExtensions != null && processor instanceof SupportsAdditionalBinaryExtensions) {
				((SupportsAdditionalBinaryExtensions) processor).setAdditionalBinaryExtensions(additionalBinaryExtensions);
			}

			// Awful hack for 4.6.0. In 5.0, the hope is to replace the processor-specific fields on this class with
			// a "Config"-type class that can be passed to each processor.
			if (processor instanceof PermissionsFileDocumentFileProcessor) {
				((PermissionsFileDocumentFileProcessor)processor).setCascadingEnabled(this.cascadePermissions);
			}
			if (processor instanceof CollectionsFileDocumentFileProcessor) {
				((CollectionsFileDocumentFileProcessor)processor).setCascadingEnabled(this.cascadeCollections);
			}
		});
	}

	/**
	 * @param reader
	 * @deprecated since 4.6.0, will be removed in 5.0.0.
	 */
	@Deprecated
	protected void applyTokenReplacerOnKnownDocumentProcessors(AbstractDocumentFileReader reader) {
		// The logic previously performed here is now handled via prepareAbstractDocumentFileReader . This is being
		// kept here solely to avoid any compilation issues in case this class was extended and this method was
		// overridden.
	}

	/**
	 * @return a set of DocumentFileProcessor objects based on how this class has been configured.
	 */
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
			processors.addAll(documentFileProcessors);
		}
		return processors;
	}

	public void addFileFilter(FileFilter fileFilter) {
		if (fileFilters == null) {
			fileFilters = new ArrayList<>();
		}
		fileFilters.add(fileFilter);
	}

	public void addDocumentFileProcessor(DocumentFileProcessor processor) {
		if (documentFileProcessors == null) {
			documentFileProcessors = new ArrayList<>();
		}
		documentFileProcessors.add(processor);
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

	public void setFileFilters(List<FileFilter> fileFilters) {
		this.fileFilters = fileFilters;
	}

	public void setDocumentFileProcessors(List<DocumentFileProcessor> documentFileProcessors) {
		this.documentFileProcessors = documentFileProcessors;
	}

	public void setAdditionalBinaryExtensions(String... additionalBinaryExtensions) {
		this.additionalBinaryExtensions = additionalBinaryExtensions;
	}

	public boolean isLogFileUris() {
		return logFileUris;
	}

	public void setLogFileUris(boolean logFileUris) {
		this.logFileUris = logFileUris;
	}

	public DocumentFileReader getDocumentFileReader() {
		return documentFileReader;
	}

	public BatchWriter getBatchWriter() {
		return batchWriter;
	}

	public TokenReplacer getTokenReplacer() {
		return tokenReplacer;
	}

	public List<FileFilter> getFileFilters() {
		return fileFilters;
	}

	public List<DocumentFileProcessor> getDocumentFileProcessors() {
		return documentFileProcessors;
	}

	public void setBatchSize(Integer batchSize) {
		this.batchSize = batchSize;
	}

	public void setBatchWriter(BatchWriter batchWriter) {
		this.batchWriter = batchWriter;
	}

	/**
	 * @param cascadeCollections
	 * @since 4.6.0
	 */
	public void setCascadeCollections(boolean cascadeCollections) {
		this.cascadeCollections = cascadeCollections;
	}

	/**
	 * @param cascadePermissions
	 * @since 4.6.0
	 */
	public void setCascadePermissions(boolean cascadePermissions) {
		this.cascadePermissions = cascadePermissions;
	}

	/**
	 * @return
	 * @since 4.6.0
	 */
	public boolean isCascadeCollections() {
		return cascadeCollections;
	}

	/**
	 * @return
	 * @since 4.6.0
	 */
	public boolean isCascadePermissions() {
		return cascadePermissions;
	}
}
