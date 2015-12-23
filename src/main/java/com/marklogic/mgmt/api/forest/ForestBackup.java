package com.marklogic.mgmt.api.forest;

import java.util.List;

import com.marklogic.mgmt.api.ApiObject;

public class ForestBackup extends ApiObject {

    private String backupId;
    private Boolean backupEnabled;
    private String backupDirectory;
    private String backupType;
    private Integer backupPeriod;
    private String backupMonthDay;
    private List<String> backupDay;
    private String backupStartDate;
    private String backupStartTime;
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

    public String getBackupMonthDay() {
        return backupMonthDay;
    }

    public void setBackupMonthDay(String backupMonthDay) {
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
