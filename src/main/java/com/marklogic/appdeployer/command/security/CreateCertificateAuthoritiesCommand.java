package com.marklogic.appdeployer.command.security;

import java.io.File;

import com.marklogic.appdeployer.command.AbstractResourceCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.rest.mgmt.ResourceManager;
import com.marklogic.rest.mgmt.security.CertificateAuthorityManager;

public class CreateCertificateAuthoritiesCommand extends AbstractResourceCommand {

    @Override
    public Integer getExecuteSortOrder() {
        return SortOrderConstants.CREATE_CERTIFICATE_AUTHORITIES;
    }

    @Override
    protected File getResourcesDir(CommandContext context) {
        return new File(context.getAppConfig().getConfigDir().getSecurityDir(), "certificate-authorities");
    }

    @Override
    protected ResourceManager getResourceManager(CommandContext context) {
        return new CertificateAuthorityManager(context.getManageClient());
    }

}
