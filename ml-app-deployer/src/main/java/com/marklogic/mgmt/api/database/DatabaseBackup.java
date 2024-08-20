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

import com.marklogic.mgmt.api.ApiObject;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class DatabaseBackup extends ApiObject {

	@XmlElement(name = "backup-id")
	private String backupId;

	@XmlElement(name = "backup-enabled")
	private Boolean backupEnabled;

	@XmlElement(name = "backup-directory")
	private String backupDirectory;

	@XmlElement(name = "backup-kek-id")
	private String backupKekId;

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

	@XmlElement(name = "max-backups")
	private Integer maxBackups;

	@XmlElement(name = "backup-schemas-database")
	private Boolean backupSchemasDatabase;

	@XmlElement(name = "backup-security-database")
	private Boolean backupSecurityDatabase;

	@XmlElement(name = "backup-triggers-database")
	private Boolean backupTriggersDatabase;

	@XmlElement(name = "include-replicas")
	private boolean includeReplicas;

	@XmlElement(name = "journal-archiving")
	private boolean journalArchiving;

	@XmlElement(name = "journal-archive-path")
	private String journalArchivePath;

	@XmlElement(name = "journal-archive-lag-limit")
	private Integer journalArchiveLagLimit;

	@XmlElement(name = "incremental-backup")
	private Boolean incrementalBackup;

	@XmlElement(name = "incremental-dir")
	private String incrementalDir;

	@XmlElement(name = "purge-journal-archive")
	private Boolean purgeJournalArchive;

	public String getBackupId() {
		return backupId;
	}

	public void setBackupId(String backupId) {
		this.backupId = backupId;
	}

	public Boolean getBackupEnabled() {
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

	public String getBackupKekId() {
		return backupKekId;
	}

	public void setBackupKekId(String backupKekId) {
		this.backupKekId = backupKekId;
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

	public Integer getMaxBackups() {
		return maxBackups;
	}

	public void setMaxBackups(Integer maxBackups) {
		this.maxBackups = maxBackups;
	}

	public Boolean getBackupSchemasDatabase() {
		return backupSchemasDatabase;
	}

	public void setBackupSchemasDatabase(Boolean backupSchemasDatabase) {
		this.backupSchemasDatabase = backupSchemasDatabase;
	}

	public Boolean getBackupSecurityDatabase() {
		return backupSecurityDatabase;
	}

	public void setBackupSecurityDatabase(Boolean backupSecurityDatabase) {
		this.backupSecurityDatabase = backupSecurityDatabase;
	}

	public Boolean getBackupTriggersDatabase() {
		return backupTriggersDatabase;
	}

	public void setBackupTriggersDatabase(Boolean backupTriggersDatabase) {
		this.backupTriggersDatabase = backupTriggersDatabase;
	}

	public boolean isIncludeReplicas() {
		return includeReplicas;
	}

	public void setIncludeReplicas(boolean includeReplicas) {
		this.includeReplicas = includeReplicas;
	}

	public boolean isJournalArchiving() {
		return journalArchiving;
	}

	public void setJournalArchiving(boolean journalArchiving) {
		this.journalArchiving = journalArchiving;
	}

	public String getJournalArchivePath() {
		return journalArchivePath;
	}

	public void setJournalArchivePath(String journalArchivePath) {
		this.journalArchivePath = journalArchivePath;
	}

	public Integer getJournalArchiveLagLimit() {
		return journalArchiveLagLimit;
	}

	public void setJournalArchiveLagLimit(Integer journalArchiveLagLimit) {
		this.journalArchiveLagLimit = journalArchiveLagLimit;
	}

	public Boolean getIncrementalBackup() {
		return incrementalBackup;
	}

	public void setIncrementalBackup(Boolean incrementalBackup) {
		this.incrementalBackup = incrementalBackup;
	}

	public String getIncrementalDir() {
		return incrementalDir;
	}

	public void setIncrementalDir(String incrementalDir) {
		this.incrementalDir = incrementalDir;
	}

	public Boolean getPurgeJournalArchive() {
		return purgeJournalArchive;
	}

	public void setPurgeJournalArchive(Boolean purgeJournalArchive) {
		this.purgeJournalArchive = purgeJournalArchive;
	}
}
