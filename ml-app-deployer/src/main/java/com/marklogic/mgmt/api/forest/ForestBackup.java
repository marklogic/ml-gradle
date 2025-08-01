/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt.api.forest;

import com.marklogic.mgmt.api.ApiObject;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class ForestBackup extends ApiObject {

	@XmlElement(name = "backup-id")
	private String backupId;

	@XmlElement(name = "backup-enabled")
	private Boolean backupEnabled;

	@XmlElement(name = "backup-directory")
	private String backupDirectory;

	@XmlElement(name = "backup-type")
	private String backupType;

	@XmlElement(name = "backup-period")
	private Integer backupPeriod;

	@XmlElement(name = "backup-month-day")
	private Integer backupMonthDay;

	@XmlElementWrapper(name = "backup-days")
	@XmlElement(name = "backup-day")
	private List<String> backupDay;

	@XmlElement(name = "backup-start-date")
	private String backupStartDate;

	@XmlElement(name = "backup-start-time")
	private String backupStartTime;

	@XmlElement(name = "backup-timestamp")
	private String backupTimestamp;

	public String getBackupId() {
		return backupId;
	}

	public void setBackupId(String backupId) {
		this.backupId = backupId;
	}

	public Boolean isBackupEnabled() {
		return backupEnabled;
	}

	public void setBackupEnabled(Boolean backupEnabled) {
		this.backupEnabled = backupEnabled;
	}

	public String getBackupDirectory() {
		return backupDirectory;
	}

	public void setBackupDirectory(String backupDirectory) {
		this.backupDirectory = backupDirectory;
	}

	public String getBackupType() {
		return backupType;
	}

	public void setBackupType(String backupType) {
		this.backupType = backupType;
	}

	public Integer getBackupPeriod() {
		return backupPeriod;
	}

	public void setBackupPeriod(Integer backupPeriod) {
		this.backupPeriod = backupPeriod;
	}

	public Integer getBackupMonthDay() {
		return backupMonthDay;
	}

	public void setBackupMonthDay(Integer backupMonthDay) {
		this.backupMonthDay = backupMonthDay;
	}

	public List<String> getBackupDay() {
		return backupDay;
	}

	public void setBackupDay(List<String> backupDay) {
		this.backupDay = backupDay;
	}

	public String getBackupStartDate() {
		return backupStartDate;
	}

	public void setBackupStartDate(String backupStartDate) {
		this.backupStartDate = backupStartDate;
	}

	public String getBackupStartTime() {
		return backupStartTime;
	}

	public void setBackupStartTime(String backupStartTime) {
		this.backupStartTime = backupStartTime;
	}

	public String getBackupTimestamp() {
		return backupTimestamp;
	}

	public void setBackupTimestamp(String backupTimestamp) {
		this.backupTimestamp = backupTimestamp;
	}

	public Boolean getBackupEnabled() {
		return backupEnabled;
	}

}
