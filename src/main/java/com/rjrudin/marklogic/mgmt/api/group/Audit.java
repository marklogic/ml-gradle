package com.rjrudin.marklogic.mgmt.api.group;

import java.util.ArrayList;
import java.util.List;

public class Audit {

    private Boolean auditEnabled;
    private String rotateAuditFiles;
    private Integer keepAuditFiles;
    private List<AuditEvent> auditEvent;
    private List<AuditRestriction> auditRestriction;

    public void addAuditEvent(AuditEvent event) {
        if (auditEvent == null) {
            auditEvent = new ArrayList<>();
        }
        auditEvent.add(event);
    }

    public void addAuditRestriction(AuditRestriction ar) {
        if (auditRestriction == null) {
            auditRestriction = new ArrayList<>();
        }
        auditRestriction.add(ar);
    }

    public Boolean getAuditEnabled() {
        return auditEnabled;
    }

    public void setAuditEnabled(Boolean auditEnabled) {
        this.auditEnabled = auditEnabled;
    }

    public String getRotateAuditFiles() {
        return rotateAuditFiles;
    }

    public void setRotateAuditFiles(String rotateAuditFiles) {
        this.rotateAuditFiles = rotateAuditFiles;
    }

    public Integer getKeepAuditFiles() {
        return keepAuditFiles;
    }

    public void setKeepAuditFiles(Integer keepAuditFiles) {
        this.keepAuditFiles = keepAuditFiles;
    }

    public List<AuditEvent> getAuditEvent() {
        return auditEvent;
    }

    public void setAuditEvent(List<AuditEvent> auditEvent) {
        this.auditEvent = auditEvent;
    }

    public List<AuditRestriction> getAuditRestriction() {
        return auditRestriction;
    }

    public void setAuditRestriction(List<AuditRestriction> auditRestriction) {
        this.auditRestriction = auditRestriction;
    }

}
