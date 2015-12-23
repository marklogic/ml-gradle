package com.marklogic.appdeployer.command.security;

import java.util.Map;

import com.marklogic.appdeployer.command.AbstractManageResourceTest;
import com.marklogic.appdeployer.command.Command;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.mgmt.ResourceManager;
import com.marklogic.mgmt.security.CertificateTemplateManager;
import com.marklogic.rest.util.Fragment;

public class ManageCertificateTemplatesTest extends AbstractManageResourceTest {

    @Override
    protected ResourceManager newResourceManager() {
        return new CertificateTemplateManager(manageClient);
    }

    @Override
    protected Command newCommand() {
        return new DeployCertificateTemplatesCommand();
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

        assertTemporaryCertificateCanBeGenerated();
    }

    private void assertTemporaryCertificateCanBeGenerated() {
        final String commonName = "localhost";

        CertificateTemplateManager mgr = new CertificateTemplateManager(manageClient);

        Fragment response = mgr.getCertificatesForTemplate("sample-app-template");
        assertFalse("The template shouldn't have any certificates yet", response.elementExists("/node()/node()"));

        GenerateTemporaryCertificateCommand gtcc = new GenerateTemporaryCertificateCommand();
        gtcc.setTemplateIdOrName("sample-app-template");
        gtcc.setCommonName(commonName);
        gtcc.execute(new CommandContext(appConfig, manageClient, adminManager));

        response = mgr.getCertificatesForTemplate("sample-app-template");
        assertTrue("The template should now have a certificate",
                response.elementExists("/msec:certificate-list/msec:certificate"));
    }

    @Override
    protected void afterResourcesCreatedAgain() {
        Map<String, String> customTokens = appConfig.getCustomTokens();
        String key = "%%certificate-templates-id-sample-app-template%%";
        assertNotNull("Verifying that the cert template ID is stored on an update as well", customTokens.get(key));
    }

}
