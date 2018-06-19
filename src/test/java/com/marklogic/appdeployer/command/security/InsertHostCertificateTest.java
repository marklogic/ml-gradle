package com.marklogic.appdeployer.command.security;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.mgmt.resource.security.CertificateTemplateManager;
import com.marklogic.rest.util.Fragment;
import org.junit.Test;

import java.io.File;
import java.util.List;


public class InsertHostCertificateTest extends AbstractAppDeployerTest {

	private final static String TEMPLATE_NAME = "sample-app-certificate-template";

	@Test
	public void test() {
		appConfig.setConfigDir(new ConfigDir(new File("src/test/resources/sample-app/host-certificates")));

		initializeAppDeployer(
			new DeployCertificateAuthoritiesCommand(),
			new DeployCertificateTemplatesCommand(),
			new InsertCertificateHostsTemplateCommand());

		CertificateTemplateManager mgr = new CertificateTemplateManager(manageClient);

		try {
			deploySampleApp();
			verifyHostCertificateWasInserted(mgr);

			// Make sure nothing breaks by deploying it again
			deploySampleApp();
			verifyHostCertificateWasInserted(mgr);
		} finally {
			/**
			 * TODO Deleting certificate authorities in ML 9.0-5 via the Manage API doesn't appear to be working, so
			 * the certificate authority that's created by this class is left over.
			 */
			undeploySampleApp();

			List<String> templateNames = mgr.getAsXml().getListItemNameRefs();
			assertFalse(templateNames.contains(TEMPLATE_NAME));
		}
	}

	private void verifyHostCertificateWasInserted(CertificateTemplateManager mgr) {
		List<String> templateNames = mgr.getAsXml().getListItemNameRefs();
		assertTrue(templateNames.contains(TEMPLATE_NAME));

		Fragment xml = mgr.getCertificatesForTemplate(TEMPLATE_NAME);
		assertEquals("host1.marklogic.com", xml.getElementValue("/msec:certificate-list/msec:certificate/msec:host-name"));
		assertEquals("MarkLogicBogusCA", xml.getElementValue("/msec:certificate-list/msec:certificate/cert:cert/cert:issuer/cert:commonName"));
	}
}
