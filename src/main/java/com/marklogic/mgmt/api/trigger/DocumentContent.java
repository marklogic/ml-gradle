package com.marklogic.mgmt.api.trigger;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class DocumentContent {

	@XmlElement(name = "update-kind")
	private String updateKind;

	public DocumentContent() {
	}

	public DocumentContent(String updateKind) {
		this.updateKind = updateKind;
	}

	public String getUpdateKind() {
		return updateKind;
	}

	public void setUpdateKind(String updateKind) {
		this.updateKind = updateKind;
	}
}
