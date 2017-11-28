package com.marklogic.client.ext.file;

import com.marklogic.client.ext.helper.LoggingObject;
import org.springframework.util.ClassUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines some generic features for the DefaultDocumentFileReader and JarDocumentFileReader.
 */
public abstract class AbstractDocumentFileReader extends LoggingObject {

	private List<DocumentFileProcessor> documentFileProcessors = new ArrayList<>();
	private FormatDocumentFileProcessor formatDocumentFileProcessor = new FormatDocumentFileProcessor();

	protected AbstractDocumentFileReader() {
		documentFileProcessors.add(formatDocumentFileProcessor);
	}

	/**
	 * Retrieves a DocumentFileProcessor with the given short class name. Useful for when you want to customize a
	 * particular processor.
	 *
	 * @param classShortName
	 * @return
	 */
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

	protected DocumentFile processDocumentFile(DocumentFile documentFile) {
		for (DocumentFileProcessor processor : documentFileProcessors) {
			try {
				if (logger.isTraceEnabled()) {
					logger.trace(String.format("Applying processor %s to DocumentFile with URI: %s", processor.getClass().getName(), documentFile.getUri()));
				}
				documentFile = processor.processDocumentFile(documentFile);
			} catch (Exception e) {
				logger.error("Error while processing document file; file: " + documentFile.getFile().getAbsolutePath()
					+ "; cause: " + e.getMessage(), e);
			}
			if (documentFile == null) {
				break;
			}
		}
		return documentFile;
	}

	public List<DocumentFileProcessor> getDocumentFileProcessors() {
		return documentFileProcessors;
	}

	public void setDocumentFileProcessors(List<DocumentFileProcessor> documentFileProcessors) {
		this.documentFileProcessors = documentFileProcessors;
	}

	public FormatDocumentFileProcessor getFormatDocumentFileProcessor() {
		return formatDocumentFileProcessor;
	}

	public void setFormatDocumentFileProcessor(FormatDocumentFileProcessor formatDocumentFileProcessor) {
		this.formatDocumentFileProcessor = formatDocumentFileProcessor;
	}
}
