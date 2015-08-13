package com.rjrudin.marklogic.appdeployer.command.security;

import java.util.Map;

import com.rjrudin.marklogic.appdeployer.command.AbstractManageResourceTest;
import com.rjrudin.marklogic.appdeployer.command.Command;
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

    @Override
    protected void afterResourcesCreated() {
        Map<String, String> customTokens = appConfig.getCustomTokens();
        String key = "%%certificate-templates-id-sample-app-template%%";
        assertNotNull(
                "The cert template ID should have been stored in the tokens map so that it can be referenced in an HTTP server file",
                customTokens.get(key));

        // Clear out the key so we can verify it's set again during the second deploy
        customTokens.remove(key);
    }

    @Override
    protected void afterResourcesCreatedAgain() {
        Map<String, String> customTokens = appConfig.getCustomTokens();
        String key = "%%certificate-templates-id-sample-app-template%%";
        assertNotNull("Verifying that the cert template ID is stored on an update as well", customTokens.get(key));
    }

}
