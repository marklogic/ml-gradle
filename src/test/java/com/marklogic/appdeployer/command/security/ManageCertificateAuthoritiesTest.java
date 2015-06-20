package com.marklogic.appdeployer.command.security;

import com.marklogic.appdeployer.command.AbstractManageResourceTest;
import com.marklogic.appdeployer.command.Command;
import com.marklogic.rest.mgmt.ResourceManager;
import com.marklogic.rest.mgmt.security.CertificateAuthorityManager;

public class ManageCertificateAuthoritiesTest extends AbstractManageResourceTest {

    @Override
    protected ResourceManager newResourceManager() {
        return new CertificateAuthorityManager(manageClient);
    }

    @Override
    protected Command newCommand() {
        return new CreateCertificateAuthoritiesCommand();
    }

    @Override
    protected String[] getResourceNames() {
        return new String[] {};
    }

}
