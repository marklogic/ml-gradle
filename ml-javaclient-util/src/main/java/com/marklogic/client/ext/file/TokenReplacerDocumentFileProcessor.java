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
import com.marklogic.client.io.Format;
import com.marklogic.client.ext.tokenreplacer.TokenReplacer;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;

/**
 * Processor that delegates to a TokenReplacer for replacing tokens in the content of a DocumentFile. In order to
 * replace tokens, the File must first be read in as a String. After tokens are replaced, the String is set back on
 * the DocumentFile via setModifiedContent.
 */
public class TokenReplacerDocumentFileProcessor extends LoggingObject implements DocumentFileProcessor {

	private TokenReplacer tokenReplacer;

	public TokenReplacerDocumentFileProcessor(TokenReplacer tokenReplacer) {
		this.tokenReplacer = tokenReplacer;
	}

	@Override
	public DocumentFile processDocumentFile(DocumentFile documentFile) {
		if (tokenReplacer != null && moduleCanBeReadAsString(documentFile.getFormat())) {
			String text = documentFile.getModifiedContent();
			if (text == null) {
				Resource resource = documentFile.getResource();
				if (resource != null) {
					try {
						text = new String(FileCopyUtils.copyToByteArray(resource.getInputStream()));
					} catch (IOException ie) {
						logger.warn("Unable to replace tokens in file: " + documentFile.getUri() + "; cause: " + ie.getMessage());
					}
				}
			}
			if (text != null) {
				text = tokenReplacer.replaceTokens(text);
				documentFile.setModifiedContent(text);
			}
		}
		return documentFile;
	}

	protected boolean moduleCanBeReadAsString(Format format) {
		return format != null && (format.equals(Format.JSON) || format.equals(Format.TEXT)
			|| format.equals(Format.XML));
	}
}
