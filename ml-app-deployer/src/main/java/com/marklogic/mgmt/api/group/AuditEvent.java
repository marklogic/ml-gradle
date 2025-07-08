/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt.api.group;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class AuditEvent {

	@XmlElement(name = "audit-event-name")
	private String auditEventName;

	@XmlElement(name = "audit-event-enabled")
	private Boolean auditEventEnabled;

	public String getAuditEventName() {
		return auditEventName;
	}

	public void setAuditEventName(String auditEventName) {
		this.auditEventName = auditEventName;
	}

	public Boolean getAuditEventEnabled() {
		return auditEventEnabled;
	}

	public void setAuditEventEnabled(Boolean auditEventEnabled) {
		this.auditEventEnabled = auditEventEnabled;
	}
}
