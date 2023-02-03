/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.mgmt.api.group;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

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
