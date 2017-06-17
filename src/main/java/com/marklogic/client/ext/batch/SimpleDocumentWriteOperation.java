package com.marklogic.client.ext.batch;

import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.io.marker.DocumentMetadataWriteHandle;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of DocumentWriteOperation for a simple use case of inserting a document with a URI, a String
 * body, and zero or more collections.
 */
public class SimpleDocumentWriteOperation implements DocumentWriteOperation {

	private String uri;
	private String[] collections;
	private String content;
	private Map<String, DocumentMetadataHandle.Capability[]> permissions;

	public SimpleDocumentWriteOperation(String uri, String content, String... collections) {
		this.uri = uri;
		this.content = content;
		this.collections = collections;
	}

	public SimpleDocumentWriteOperation addPermissions(String role, DocumentMetadataHandle.Capability... capabilities) {
		if (permissions == null) {
			permissions = new HashMap<>();
		}
		permissions.put(role, capabilities);
		return this;
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
		if (permissions != null) {
			for (String role : permissions.keySet()) {
				h.withPermission(role, permissions.get(role));
			}
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

	public void setCollections(String[] collections) {
		this.collections = collections;
	}

	public void setPermissions(Map<String, DocumentMetadataHandle.Capability[]> permissions) {
		this.permissions = permissions;
	}
}
