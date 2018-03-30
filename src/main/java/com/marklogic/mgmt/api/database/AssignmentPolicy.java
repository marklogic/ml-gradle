package com.marklogic.mgmt.api.database;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class AssignmentPolicy {

	@XmlElement(name = "assignment-policy-name")
	private String assignmentPolicyName;

	public String getAssignmentPolicyName() {
		return assignmentPolicyName;
	}

	public void setAssignmentPolicyName(String assignmentPolicyName) {
		this.assignmentPolicyName = assignmentPolicyName;
	}
}
