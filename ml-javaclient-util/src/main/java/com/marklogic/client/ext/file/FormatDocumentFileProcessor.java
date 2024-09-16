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

import java.util.Arrays;

/**
 * Delegates to DefaultDocumentFormatGetter by default for determining what Format to use for the File in a given
 * DocumentFile.
 */
public class FormatDocumentFileProcessor extends LoggingObject
	implements DocumentFileProcessor, SupportsAdditionalBinaryExtensions {

	private FormatGetter formatGetter;

	public FormatDocumentFileProcessor() {
		this(new DefaultDocumentFormatGetter());
	}

	public FormatDocumentFileProcessor(FormatGetter formatGetter) {
		this.formatGetter = formatGetter;
	}

	@Override
	public DocumentFile processDocumentFile(DocumentFile documentFile) {
		Format format = formatGetter.getFormat(documentFile.getResource());
		if (format != null) {
			documentFile.setFormat(format);
		}
		return documentFile;
	}

	@Override
	public void setAdditionalBinaryExtensions(String[] extensions) {
		if (formatGetter instanceof DefaultDocumentFormatGetter) {
			DefaultDocumentFormatGetter ddfg = (DefaultDocumentFormatGetter) formatGetter;
			for (String ext : extensions) {
				ddfg.getBinaryExtensions().add(ext);
			}
		} else {
			logger.warn("FormatGetter is not an instanceof DefaultDocumentFormatGetter, " +
				"so unable to add additionalBinaryExtensions: " + Arrays.asList(extensions));
		}
	}

	public FormatGetter getFormatGetter() {
		return formatGetter;
	}

	public void setFormatGetter(FormatGetter formatGetter) {
		this.formatGetter = formatGetter;
	}
}
