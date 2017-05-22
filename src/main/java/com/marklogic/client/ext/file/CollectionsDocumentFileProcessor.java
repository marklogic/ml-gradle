package com.marklogic.client.ext.file;

public class CollectionsDocumentFileProcessor implements DocumentFileProcessor {

	private String[] collections;

	public CollectionsDocumentFileProcessor(String... collections) {
		this.collections = collections;
	}

	@Override
	public DocumentFile processDocumentFile(DocumentFile documentFile) {
		if (collections != null && documentFile.getDocumentMetadata() != null) {
			documentFile.getDocumentMetadata().withCollections(collections);
		}
		return documentFile;
	}
}
