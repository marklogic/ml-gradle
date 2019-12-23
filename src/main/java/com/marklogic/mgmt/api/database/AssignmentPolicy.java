package com.marklogic.mgmt.api.database;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class AssignmentPolicy {

	@XmlElement(name = "assignment-policy-name")
	private String assignmentPolicyName;

	@XmlElement(name = "lower-bound-included")
	private Boolean lowerBoundIncluded;

	@XmlElement(name = "default-partition")
	private Integer defaultPartition;

	public String getAssignmentPolicyName() {
		return assignmentPolicyName;
	}

	public void setAssignmentPolicyName(String assignmentPolicyName) {
		this.assignmentPolicyName = assignmentPolicyName;
	}

	public Boolean getLowerBoundIncluded() {
		return lowerBoundIncluded;
	}

	public void setLowerBoundIncluded(Boolean lowerBoundIncluded) {
		this.lowerBoundIncluded = lowerBoundIncluded;
	}

	public Integer getDefaultPartition() {
		return defaultPartition;
	}

	public void setDefaultPartition(Integer defaultPartition) {
		this.defaultPartition = defaultPartition;
	}
}
