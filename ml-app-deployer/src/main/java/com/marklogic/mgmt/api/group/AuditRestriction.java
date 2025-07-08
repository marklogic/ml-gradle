/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt.api.group;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class AuditRestriction {

	@XmlElement(name = "audit-restriction-name")
	private String auditRestrictionName;

	@XmlElement(name = "audit-restriction-type")
	private String auditRestrictionType;

	@XmlElement(name = "audit-restriction-items")
	private String auditRestrictionItems;

	public String getAuditRestrictionName() {
		return auditRestrictionName;
	}

	public void setAuditRestrictionName(String auditRestrictionName) {
		this.auditRestrictionName = auditRestrictionName;
	}

	public String getAuditRestrictionType() {
		return auditRestrictionType;
	}

	public void setAuditRestrictionType(String auditRestrictionType) {
		this.auditRestrictionType = auditRestrictionType;
	}

	public String getAuditRestrictionItems() {
		return auditRestrictionItems;
	}

	public void setAuditRestrictionItems(String auditRestrictionItems) {
		this.auditRestrictionItems = auditRestrictionItems;
	}
}
