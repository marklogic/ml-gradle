package com.marklogic.appdeployer.command.security;

import com.marklogic.appdeployer.command.AbstractResourceCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.security.CertificateTemplateManager;

import java.io.File;

public class DeployCertificateTemplatesCommand extends AbstractResourceCommand {

    public DeployCertificateTemplatesCommand() {
        setExecuteSortOrder(SortOrderConstants.DEPLOY_CERTIFICATE_TEMPLATES);
        setUndoSortOrder(SortOrderConstants.DELETE_CERTIFICATE_TEMPLATES);

        // Since an HTTP server file needs to refer to a certificate template by its ID, this is set to true
        setStoreResourceIdsAsCustomTokens(true);
    }

    @Override
    protected File[] getResourceDirs(CommandContext context) {
    	return findResourceDirs(context, configDir -> configDir.getCertificateTemplatesDir());
    }

    @Override
    protected ResourceManager getResourceManager(CommandContext context) {
        return new CertificateTemplateManager(context.getManageClient());
    }

}
