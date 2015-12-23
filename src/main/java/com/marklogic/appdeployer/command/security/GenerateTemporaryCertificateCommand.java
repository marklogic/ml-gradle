package com.marklogic.appdeployer.command.security;

import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.security.CertificateTemplateManager;

public class GenerateTemporaryCertificateCommand extends AbstractCommand {

    private String templateIdOrName;
    private String commonName;
    private int validFor = 365;
    private String dnsName;
    private String ipAddress;
    private boolean ifNecessary = true;

    public GenerateTemporaryCertificateCommand() {
        setExecuteSortOrder(SortOrderConstants.GENERATE_TEMPORARY_CERTIFICATE);
    }

    @Override
    public void execute(CommandContext context) {
        if (templateIdOrName != null) {
            CertificateTemplateManager mgr = new CertificateTemplateManager(context.getManageClient());
            mgr.generateTemporaryCertificate(templateIdOrName, commonName, validFor, dnsName, ipAddress, ifNecessary);
        }
    }

    public String getTemplateIdOrName() {
        return templateIdOrName;
    }

    public void setTemplateIdOrName(String templateIdOrName) {
        this.templateIdOrName = templateIdOrName;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public int getValidFor() {
        return validFor;
    }

    public void setValidFor(int validFor) {
        this.validFor = validFor;
    }

    public String getDnsName() {
        return dnsName;
    }

    public void setDnsName(String dnsName) {
        this.dnsName = dnsName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public boolean isIfNecessary() {
        return ifNecessary;
    }

    public void setIfNecessary(boolean ifNecessary) {
        this.ifNecessary = ifNecessary;
    }

}
