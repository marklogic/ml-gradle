package com.marklogic.mgmt.api.group;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class Audit {

	@XmlElement(name = "audit-enabled")
    private Boolean auditEnabled;

	@XmlElement(name = "rotate-audit-files")
    private String rotateAuditFiles;

	@XmlElement(name = "keep-audit-files")
    private Integer keepAuditFiles;

	@XmlElementWrapper(name = "audit-events")
	@XmlElement(name = "audit-event")
    private List<AuditEvent> auditEvent;

	@XmlElementWrapper(name = "audit-restrictions")
	@XmlElement(name = "audit-restriction")
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
