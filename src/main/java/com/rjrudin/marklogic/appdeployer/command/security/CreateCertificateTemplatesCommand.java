package com.rjrudin.marklogic.appdeployer.command.security;

import java.io.File;

import com.rjrudin.marklogic.appdeployer.command.AbstractResourceCommand;
import com.rjrudin.marklogic.appdeployer.command.CommandContext;
import com.rjrudin.marklogic.appdeployer.command.SortOrderConstants;
import com.rjrudin.marklogic.mgmt.ResourceManager;
import com.rjrudin.marklogic.mgmt.security.CertificateTemplateManager;

public class CreateCertificateTemplatesCommand extends AbstractResourceCommand {

    public CreateCertificateTemplatesCommand() {
        setExecuteSortOrder(SortOrderConstants.CREATE_CERTIFICATE_TEMPLATES);

        // Since an HTTP server file needs to refer to a certificate template by its ID, this is set to true
        setStoreResourceIdsAsCustomTokens(true);
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
