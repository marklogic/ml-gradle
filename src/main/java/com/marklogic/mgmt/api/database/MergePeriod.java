package com.marklogic.mgmt.api.database;

import com.marklogic.mgmt.api.ApiObject;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class MergePeriod extends ApiObject {

	@XmlElement(name = "start-date")
	private String startDate;

	@XmlElement(name = "start-time")
	private String startTime;

	@XmlElement(name = "end-date")
	private String endDate;

	@XmlElement(name = "end-time")
	private String endTime;

	@XmlElement(name = "duration")
	private String duration;

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}
}
