package com.marklogic.client.ext.file;

import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.io.marker.DocumentMetadataWriteHandle;

import java.io.File;
import java.nio.file.Path;

/**
 * Encapsulates a file that should be written to MarkLogic as a single document. Implements DocumentWriteOperation so
 * that it can be easily written via the Java Client API.
 *
 * The modifiedContent allows for the content of this DocumentWriteOperation to be set via that property instead of via
 * the File. The assumption is that something like a DocumentFileProcessor has read in the contents of the File used to
 * construct this class,
 */
public class DocumentFile implements DocumentWriteOperation {

	private String uri;
	private File file;
	private Format format;
	private DocumentMetadataHandle documentMetadata;
	private String temporalDocumentURI;
	private String modifiedContent;
	private Path rootPath;

	public DocumentFile(String uri, File file) {
		this.uri = uri;
		this.file = file;
		this.documentMetadata = new DocumentMetadataHandle();
	}

	@Override
	public String getUri() {
		return uri;
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
		if (modifiedContent != null) {
			StringHandle h = new StringHandle(modifiedContent);
			return format != null ? h.withFormat(format) : h;
		}
		FileHandle h = new FileHandle(file);
		return format != null ? h.withFormat(format) : h;
	}

	@Override
	public String getTemporalDocumentURI() {
		return temporalDocumentURI;
	}

	public String getFileExtension() {
		if (file != null) {
			String name = file.getName();
			int pos = name.lastIndexOf('.');
			return pos < 0 ? name : name.substring(pos + 1);
		}
		return null;
	}

	public File getFile() {
		return file;
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

	public void setTemporalDocumentURI(String temporalDocumentURI) {
		this.temporalDocumentURI = temporalDocumentURI;
	}

	public void setModifiedContent(String modifiedContent) {
		this.modifiedContent = modifiedContent;
	}

	public String getModifiedContent() {
		return modifiedContent;
	}

	public Path getRootPath() {
		return rootPath;
	}

	/**
	 * Optional to set - the Path that this File was loaded relative to
	 * @param rootPath
	 */
	public void setRootPath(Path rootPath) {
		this.rootPath = rootPath;
	}
}
