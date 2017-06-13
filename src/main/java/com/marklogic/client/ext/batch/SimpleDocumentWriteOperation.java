package com.marklogic.client.ext.batch;

import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.io.marker.DocumentMetadataWriteHandle;

/**
 * Implementation of DocumentWriteOperation for a simple use case of inserting a document with a URI, a String
 * body, and zero or more collections.
 */
public class SimpleDocumentWriteOperation implements DocumentWriteOperation {

	private String uri;
	private String[] collections;
	private String content;

	public SimpleDocumentWriteOperation(String uri, String content, String... collections) {
		this.uri = uri;
		this.content = content;
		this.collections = collections;
	}

	@Override
	public OperationType getOperationType() {
		return OperationType.DOCUMENT_WRITE;
	}

	@Override
	public String getUri() {
		return uri;
	}

	@Override
	public DocumentMetadataWriteHandle getMetadata() {
		DocumentMetadataHandle h = new DocumentMetadataHandle();
		if (collections != null) {
			h.withCollections(collections);
		}
		return h;
	}

	@Override
	public AbstractWriteHandle getContent() {
		return new StringHandle(content);
	}

	@Override
	public String getTemporalDocumentURI() {
		return null;
	}
}
