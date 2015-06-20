package com.marklogic.appdeployer.command.security;

import org.junit.Ignore;

import com.marklogic.appdeployer.command.AbstractManageResourceTest;
import com.marklogic.appdeployer.command.Command;
import com.marklogic.rest.mgmt.ResourceManager;
import com.marklogic.rest.mgmt.security.CertificateTemplateManager;

@Ignore("Not passing yet because the mgmt API for a template won't accept a template name")
public class ManageCertificateTemplatesTest extends AbstractManageResourceTest {

    @Override
    protected ResourceManager newResourceManager() {
        return new CertificateTemplateManager(manageClient);
    }

    @Override
    protected Command newCommand() {
        return new CreateCertificateTemplatesCommand();
    }

    @Override
    protected String[] getResourceNames() {
        return new String[] { "sample-app-template" };
    }

}
