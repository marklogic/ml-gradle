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
package com.marklogic.appdeployer.command.security;

import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.resource.security.CertificateTemplateManager;

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
