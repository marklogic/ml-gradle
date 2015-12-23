package com.marklogic.appdeployer.command.security;

import java.io.File;

import com.marklogic.appdeployer.command.AbstractResourceCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.ResourceManager;
import com.marklogic.mgmt.security.CertificateTemplateManager;

public class DeployCertificateTemplatesCommand extends AbstractResourceCommand {

    public DeployCertificateTemplatesCommand() {
        setExecuteSortOrder(SortOrderConstants.DEPLOY_CERTIFICATE_TEMPLATES);
        setUndoSortOrder(SortOrderConstants.DELETE_CERTIFICATE_TEMPLATES);

        // Since an HTTP server file needs to refer to a certificate template by its ID, this is set to true
        setStoreResourceIdsAsCustomTokens(true);
    }

    @Override
    protected File[] getResourceDirs(CommandContext context) {
        return new File[] { new File(context.getAppConfig().getConfigDir().getSecurityDir(), "certificate-templates") };
    }

    @Override
    protected ResourceManager getResourceManager(CommandContext context) {
        return new CertificateTemplateManager(context.getManageClient());
    }

}
