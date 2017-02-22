package com.marklogic.client.file;

import com.marklogic.client.batch.BatchWriter;
import com.marklogic.client.helper.LoggingObject;
import org.springframework.util.ClassUtils;

import java.util.ArrayList;
import java.util.List;

public class DefaultFileLoader extends LoggingObject implements FileLoader {

	private DocumentFileFinder documentFileFinder;
	private BatchWriter batchWriter;
	private List<DocumentFileProcessor> documentFileProcessors;

	public DefaultFileLoader(BatchWriter batchWriter) {
		this(batchWriter, new DefaultDocumentFileFinder());
	}

	/**
	 * @param batchWriter This is assumed to have already been initialized, as this class is not interested in managing
	 *                    the lifecycle of a BatchWriter
	 * @param documentFileFinder
	 */
	public DefaultFileLoader(BatchWriter batchWriter, DocumentFileFinder documentFileFinder) {
		this.batchWriter = batchWriter;
		this.documentFileFinder = documentFileFinder;
		initializeDocumentFileProcessors();
	}

	protected void initializeDocumentFileProcessors() {
		documentFileProcessors = new ArrayList<>();
		documentFileProcessors.add(new CollectionsDocumentFileProcessor());
		documentFileProcessors.add(new FormatDocumentFileProcessor());
	}

	@Override
	public List<DocumentFile> loadFiles(String... paths) {
		List<DocumentFile> documentFiles = documentFileFinder.findDocumentFiles(paths);
		documentFiles = processDocumentFiles(documentFiles);
		batchWriter.write(documentFiles);
		return documentFiles;
	}

	protected List<DocumentFile> processDocumentFiles(List<DocumentFile> documentFiles) {
		List<DocumentFile> newFiles = new ArrayList<>();
		for (DocumentFile file : documentFiles) {
			for (DocumentFileProcessor processor : documentFileProcessors) {
				file = processor.processDocumentFile(file);
				if (file == null) {
					break;
				}
			}
			if (file != null) {
				newFiles.add(file);
			}
		}
		return newFiles;
	}
	public DocumentFileProcessor getDocumentFileProcessor(String classShortName) {
		for (DocumentFileProcessor processor : documentFileProcessors) {
			if (ClassUtils.getShortName(processor.getClass()).equals(classShortName)) {
				return processor;
			}
		}
		return null;
	}

	public void addDocumentFileProcessor(DocumentFileProcessor processor) {
		if (documentFileProcessors == null) {
			documentFileProcessors = new ArrayList<>();
		}
		documentFileProcessors.add(processor);
	}

	public List<DocumentFileProcessor> getDocumentFileProcessors() {
		return documentFileProcessors;
	}

	public void setDocumentFileProcessors(List<DocumentFileProcessor> documentFileProcessors) {
		this.documentFileProcessors = documentFileProcessors;
	}
}
