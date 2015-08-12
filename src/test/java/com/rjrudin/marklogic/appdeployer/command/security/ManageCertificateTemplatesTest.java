package com.rjrudin.marklogic.appdeployer.command.security;

import com.rjrudin.marklogic.appdeployer.command.AbstractManageResourceTest;
import com.rjrudin.marklogic.appdeployer.command.Command;
import com.rjrudin.marklogic.mgmt.ResourceManager;
import com.rjrudin.marklogic.mgmt.security.CertificateTemplateManager;

public class ManageCertificateTemplatesTest extends AbstractManageResourceTest {

    //@Test
    public void rob() {
        manageClient.putJson("/manage/v2/servers/sample-project/properties?group-id=Default",
                "{\"ssl-certificate-template\":\"10426856940027527644\"}");
    }

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
