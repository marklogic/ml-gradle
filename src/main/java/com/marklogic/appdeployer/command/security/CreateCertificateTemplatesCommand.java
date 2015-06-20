package com.marklogic.appdeployer.command.security;

import java.io.File;

import com.marklogic.appdeployer.command.AbstractResourceCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.rest.mgmt.ResourceManager;
import com.marklogic.rest.mgmt.security.CertificateTemplateManager;

public class CreateCertificateTemplatesCommand extends AbstractResourceCommand {

    @Override
    public Integer getExecuteSortOrder() {
        return SortOrderConstants.CREATE_CERTIFICATE_TEMPLATES;
    }

    @Override
    protected File getResourcesDir(CommandContext context) {
        return new File(context.getAppConfig().getConfigDir().getSecurityDir(), "certificate-templates");
    }

    @Override
    protected ResourceManager getResourceManager(CommandContext context) {
        return new CertificateTemplateManager(context.getManageClient());
    }

}
