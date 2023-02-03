/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.appdeployer.command.security;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.mgmt.resource.security.CertificateTemplateManager;
import com.marklogic.rest.util.Fragment;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class InsertHostCertificateTest extends AbstractAppDeployerTest {

	private final static String TEMPLATE_NAME = "sample-app-certificate-template";
	private final static String CERTIFICATE_HOSTNAME = "host1.marklogic.com";

	@Test
	public void extractHostNameFromEvalResponse() {
		String hostName = new InsertCertificateHostsTemplateCommand().extractHostNameFromEvalResponse("--8f04db15a117ed37\n" +
			"Content-Type: text/plain\n" +
			"X-Primitive: string\n" +
			"\n" +
			"MarkLogicBogusCA\n" +
			"--8f04db15a117ed37--");
		assertEquals("MarkLogicBogusCA", hostName);
	}

	@Test
	public void unexpectedEvalResponse() {
		try {
			new InsertCertificateHostsTemplateCommand().extractHostNameFromEvalResponse("--8f04db15a117ed37\n" +
				"Content-Type: text/plain\n" +
				"\n" +
				"MarkLogicBogusCA\n" +
				"--8f04db15a117ed37--");
			fail("Should have failed because X-Primitive: string is missing");
		} catch (IllegalArgumentException ex) {
			assertTrue(ex.getMessage().contains("did not find: X-Primitive: string"));
		}
	}

	@Test
	public void anotherUnexpectedEvalResponse() {
		try {
			new InsertCertificateHostsTemplateCommand().extractHostNameFromEvalResponse("--8f04db15a117ed37\n" +
				"Content-Type: text/plain\n" +
				"X-Primitive: string\n" +
				"\n" +
				"MarkLogicBogusCA\n");
			fail("Should have failed because there's no '--' after X-Primitive: string");
		} catch (IllegalArgumentException ex) {
			assertTrue(ex.getMessage().contains("did not find '--'"));
		}
	}

	@Test
	public void extractHostNameFromFile() {
		File certFile = new File("src/test/resources/sample-app/host-certificates/security/certificate-templates" +
			"/host-certificates/sample-app-certificate-template/host1.marklogic.com.crt");
		String hostName = new InsertCertificateHostsTemplateCommand().getCertificateHostName(certFile, manageClient);
		assertEquals("host1.marklogic.com", hostName);
	}

	@Test
	public void insertCertificateAndVerify() {
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
		assertEquals(CERTIFICATE_HOSTNAME, xml.getElementValue("/msec:certificate-list/msec:certificate/msec:host-name"));
		assertEquals("MarkLogicBogusCA", xml.getElementValue("/msec:certificate-list/msec:certificate/cert:cert/cert:issuer/cert:commonName"));
	}
}
