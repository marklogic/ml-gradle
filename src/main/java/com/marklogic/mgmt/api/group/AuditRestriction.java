package com.marklogic.mgmt.api.group;

public class AuditRestriction {

    private String auditRestrictionName;
    private String auditRestrictionType;
    private String auditRestrictionItems;

    public String getAuditRestrictionName() {
        return auditRestrictionName;
    }

    public void setAuditRestrictionName(String auditRestrictionName) {
        this.auditRestrictionName = auditRestrictionName;
    }

    public String getAuditRestrictionType() {
        return auditRestrictionType;
    }

    public void setAuditRestrictionType(String auditRestrictionType) {
        this.auditRestrictionType = auditRestrictionType;
    }

    public String getAuditRestrictionItems() {
        return auditRestrictionItems;
    }

    public void setAuditRestrictionItems(String auditRestrictionItems) {
        this.auditRestrictionItems = auditRestrictionItems;
    }
}
