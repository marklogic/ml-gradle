package com.marklogic.mgmt.api.group;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

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
