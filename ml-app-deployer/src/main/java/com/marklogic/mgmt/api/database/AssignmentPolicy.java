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
package com.marklogic.mgmt.api.database;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

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
