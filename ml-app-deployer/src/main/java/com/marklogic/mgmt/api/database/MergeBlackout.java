/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt.api.database;

import com.marklogic.mgmt.api.ApiObject;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class MergeBlackout extends ApiObject {

	@XmlElement(name = "blackout-type")
	private String blackoutType;

	private Integer limit;

	@XmlElement(name = "merge-priority")
	private String mergePriority;

	@XmlElementWrapper(name = "days")
	private List<String> day;

	private MergePeriod period;

	public String getBlackoutType() {
		return blackoutType;
	}

	public void setBlackoutType(String blackoutType) {
		this.blackoutType = blackoutType;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public String getMergePriority() {
		return mergePriority;
	}

	public void setMergePriority(String mergePriority) {
		this.mergePriority = mergePriority;
	}

	public List<String> getDay() {
		return day;
	}

	public void setDay(List<String> day) {
		this.day = day;
	}

	public MergePeriod getPeriod() {
		return period;
	}

	public void setPeriod(MergePeriod period) {
		this.period = period;
	}
}
