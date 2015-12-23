package com.marklogic.mgmt.api.group;

public class AuditEvent {

    private String auditEventName;
    private Boolean auditEventEnabled;

    public String getAuditEventName() {
        return auditEventName;
    }

    public void setAuditEventName(String auditEventName) {
        this.auditEventName = auditEventName;
    }

    public Boolean getAuditEventEnabled() {
        return auditEventEnabled;
    }

    public void setAuditEventEnabled(Boolean auditEventEnabled) {
        this.auditEventEnabled = auditEventEnabled;
    }
}
