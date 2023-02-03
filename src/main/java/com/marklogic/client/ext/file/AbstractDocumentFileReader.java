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
	private boolean catchProcessingError = false;

	protected AbstractDocumentFileReader() {
		documentFileProcessors.add(formatDocumentFileProcessor);
	}

	/**
	 * @param classShortName
	 * @return Retrieves a DocumentFileProcessor with the given short class name. Useful for when you want to customize a
	 * particular processor.
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

	/**
	 * The catchProcessingError field controls whether an exception thrown by a processor will be caught or not. Starting
	 * in 3.11.0, it defaults to false, as an exception typically indicates that the processing should stop.
	 *
	 * @param documentFile
	 * @return the result of processing the given DocumentFile; may return null
	 */
	protected DocumentFile processDocumentFile(DocumentFile documentFile) {
		for (DocumentFileProcessor processor : documentFileProcessors) {
			try {
				if (logger.isTraceEnabled()) {
					logger.trace(String.format("Applying processor %s to DocumentFile with URI: %s", processor.getClass().getName(), documentFile.getUri()));
				}
				documentFile = processor.processDocumentFile(documentFile);
			} catch (Exception e) {
				final String message = "Error while processing file: " + documentFile.getFile() + "; cause: " + e.getMessage();
				if (catchProcessingError) {
					logger.error(message, e);
				} else {
					throw new RuntimeException(message, e);
				}
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

	public boolean isCatchProcessingError() {
		return catchProcessingError;
	}

	public void setCatchProcessingError(boolean catchProcessingError) {
		this.catchProcessingError = catchProcessingError;
	}
}
