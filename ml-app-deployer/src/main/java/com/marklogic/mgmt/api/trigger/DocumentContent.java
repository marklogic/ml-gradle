/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt.api.trigger;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

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
