package com.marklogic.client.ext.file;

import com.marklogic.client.io.Format;

/**
 * Delegates to DefaultDocumentFormatGetter by default for determining what Format to use for the File in a given
 * DocumentFile.
 */
public class FormatDocumentFileProcessor implements DocumentFileProcessor {

	private FormatGetter formatGetter;

	public FormatDocumentFileProcessor() {
		this(new DefaultDocumentFormatGetter());
	}

	public FormatDocumentFileProcessor(FormatGetter formatGetter) {
		this.formatGetter = formatGetter;
	}

	@Override
	public DocumentFile processDocumentFile(DocumentFile documentFile) {
		Format format = formatGetter.getFormat(documentFile.getFile());
		if (format != null) {
			documentFile.setFormat(format);
		}
		return documentFile;
	}

	public FormatGetter getFormatGetter() {
		return formatGetter;
	}

	public void setFormatGetter(FormatGetter formatGetter) {
		this.formatGetter = formatGetter;
	}
}
