package com.marklogic.mgmt.api.trigger;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class DataEvent {

	@XmlElement(name = "collection-scope")
	private CollectionScope collectionScope;

	@XmlElement(name = "directory-scope")
	private DirectoryScope directoryScope;

	@XmlElement(name = "document-content")
	private DocumentContent documentContent;

	@XmlElement(name = "document-scope")
	private DocumentScope documentScope;

	@XmlElement(name = "property-content")
	private PropertyContent propertyContent;

	private String when;

	public DirectoryScope getDirectoryScope() {
		return directoryScope;
	}

	public void setDirectoryScope(DirectoryScope directoryScope) {
		this.directoryScope = directoryScope;
	}

	public DocumentContent getDocumentContent() {
		return documentContent;
	}

	public void setDocumentContent(DocumentContent documentContent) {
		this.documentContent = documentContent;
	}

	public String getWhen() {
		return when;
	}

	public void setWhen(String when) {
		this.when = when;
	}

	public CollectionScope getCollectionScope() {
		return collectionScope;
	}

	public void setCollectionScope(CollectionScope collectionScope) {
		this.collectionScope = collectionScope;
	}

	public DocumentScope getDocumentScope() {
		return documentScope;
	}

	public void setDocumentScope(DocumentScope documentScope) {
		this.documentScope = documentScope;
	}

	public PropertyContent getPropertyContent() {
		return propertyContent;
	}

	public void setPropertyContent(PropertyContent propertyContent) {
		this.propertyContent = propertyContent;
	}
}
