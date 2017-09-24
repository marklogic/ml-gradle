package com.marklogic.client.ext.file;

import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.io.*;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.io.marker.DocumentMetadataWriteHandle;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
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
	private Resource resource;
	private Format format;
	private DocumentMetadataHandle documentMetadata;
	private String temporalDocumentURI;
	private String modifiedContent;
	private Path rootPath;

	public DocumentFile(String uri, Resource resource) {
		init(uri, resource);
	}

	public DocumentFile(String uri, File file) {
		init(uri, new FileSystemResource(file));
	}

	private void init(String uri, Resource resource) {
		this.uri = uri;
		this.resource = resource;
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
		InputStreamHandle h = null;
		try {
			h = new InputStreamHandle(resource.getInputStream());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return format != null ? h.withFormat(format) : h;
	}

	@Override
	public String getTemporalDocumentURI() {
		return temporalDocumentURI;
	}

	public String getFileExtension() {
		if (resource != null) {
			String name = resource.getFilename();
			int pos = name.lastIndexOf('.');
			return pos < 0 ? name : name.substring(pos + 1);
		}
		return null;
	}

	public Resource getResource() {
		return resource;
	}

	public File getFile() {
		File file;
		try {
			file = resource.getFile();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
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

	public void setResource(Resource resource) {
		this.resource = resource;
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
