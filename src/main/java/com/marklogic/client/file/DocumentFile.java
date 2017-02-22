package com.marklogic.client.file;

import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.io.marker.DocumentMetadataWriteHandle;

import java.io.File;

/**
 * Encapsulates a file that should be written to MarkLogic as a single document. Implements DocumentWriteOperation so
 * that it can be easily written via the Java Client API.
 */
public class DocumentFile implements DocumentWriteOperation {

	private String uri;
	private File file;
	private Format format;
	private DocumentMetadataHandle documentMetadata;

	public DocumentFile(String uri, File file) {
		this.uri = uri;
		this.file = file;
		this.documentMetadata = new DocumentMetadataHandle();
	}

	public String getUri() {
		return uri;
	}

	public File getFile() {
		return file;
	}

	public String getFileExtension() {
		String name = file.getName();
		int pos = name.lastIndexOf('.');
		return pos < 0 ? name : name.substring(pos + 1);
	}

	@Override
	public OperationType getOperationType() {
		return OperationType.DOCUMENT_WRITE;
	}

	@Override
	public DocumentMetadataWriteHandle getMetadata() {
		return documentMetadata;
	}

	@Override
	public AbstractWriteHandle getContent() {
		FileHandle h = new FileHandle(file);
		return format != null ? h.withFormat(format) : h;
	}

	public void setFormat(Format format) {
		this.format = format;
	}

	public DocumentMetadataHandle getDocumentMetadata() {
		return documentMetadata;
	}

	public void setDocumentMetadata(DocumentMetadataHandle documentMetadata) {
		this.documentMetadata = documentMetadata;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public Format getFormat() {
		return format;
	}
}
