package com.rjrudin.marklogic.appdeployer.command.security;

import com.rjrudin.marklogic.appdeployer.command.AbstractManageResourceTest;
import com.rjrudin.marklogic.appdeployer.command.Command;
import com.rjrudin.marklogic.appdeployer.command.security.CreateCertificateTemplatesCommand;
import com.rjrudin.marklogic.mgmt.ResourceManager;
import com.rjrudin.marklogic.mgmt.security.CertificateTemplateManager;

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
