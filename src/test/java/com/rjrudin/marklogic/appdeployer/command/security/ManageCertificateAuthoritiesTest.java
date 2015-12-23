package com.rjrudin.marklogic.appdeployer.command.security;

import org.junit.Test;

import com.rjrudin.marklogic.appdeployer.AbstractAppDeployerTest;
import com.rjrudin.marklogic.mgmt.security.CertificateAuthorityManager;
import com.rjrudin.marklogic.rest.util.ResourcesFragment;

/**
 * All we can reliably do from a file-driven approach is create a certificate authority. The Management REST API in
 * 8.0-3 does not provide a way to update a certificate authority. And deleting one requires knowing its ID number, but
 * there's not a reliable way of determining that from a *.crt file. So it'll be up to a developer to delete a
 * certificate authority.
 */
public class ManageCertificateAuthoritiesTest extends AbstractAppDeployerTest {

    @Test
    public void test() {
        // Run the command to create a certificate authority
        initializeAppDeployer(new DeployCertificateAuthoritiesCommand());
        appDeployer.deploy(appConfig);

        // Get the ID of the created certificate authority
        CertificateAuthorityManager mgr = new CertificateAuthorityManager(manageClient);
        ResourcesFragment resources = mgr.getAsXml();
        String id = resources.getListItemValue("MarkLogic TX Engineering", "idref");
        assertNotNull("The certificate authority should have been created", id);

        // Delete the certificate authority
        mgr.delete(id);

        // And then verify that it's gone
        resources = mgr.getAsXml();
        id = resources.getListItemValue("MarkLogic TX Engineering", "idref");
        assertNull("The certificate authority should no longer exist", id);
    }
}
