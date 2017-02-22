package com.marklogic.client.file;

import com.marklogic.client.io.Format;
import com.marklogic.client.modulesloader.impl.FormatGetter;
import com.marklogic.client.modulesloader.xcc.DefaultDocumentFormatGetter;

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
	public boolean supportsDocumentFile(DocumentFile documentFile) {
		return true;
	}

	@Override
	public void processDocumentFile(DocumentFile documentFile) {
		Format format = formatGetter.getFormat(documentFile.getFile());
		if (format != null) {
			documentFile.setFormat(format);
		}
	}

	public FormatGetter getFormatGetter() {
		return formatGetter;
	}

	public void setFormatGetter(FormatGetter formatGetter) {
		this.formatGetter = formatGetter;
	}
}
