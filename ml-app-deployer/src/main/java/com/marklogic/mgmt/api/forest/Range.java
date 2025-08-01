/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt.api.forest;

import com.marklogic.mgmt.api.ApiObject;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Range extends ApiObject {

	@XmlElement(name = "lower-bound")
	private String lowerBound;

	@XmlElement(name = "upper-bound")
	private String upperBound;

	public String getLowerBound() {
		return lowerBound;
	}

	public void setLowerBound(String lowerBound) {
		this.lowerBound = lowerBound;
	}

	public String getUpperBound() {
		return upperBound;
	}

	public void setUpperBound(String upperBound) {
		this.upperBound = upperBound;
	}
}
