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
import com.marklogic.junit5.RequiresMarkLogic12;
import com.marklogic.mgmt.resource.security.CertificateTemplateManager;
import com.marklogic.rest.util.Fragment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.io.File;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


class InsertHostCertificateTest extends AbstractAppDeployerTest {

	private final static String TEMPLATE_NAME = "sample-app-certificate-template";
	private final static String CERTIFICATE_HOSTNAME = "host1.marklogic.com";

	@Test
	void extractHostNameFromEvalResponse() {
		String hostName = new InsertCertificateHostsTemplateCommand().extractHostNameFromEvalResponse("--8f04db15a117ed37\n" +
			"Content-Type: text/plain\n" +
			"X-Primitive: string\n" +
			"\n" +
			"MarkLogicBogusCA\n" +
			"--8f04db15a117ed37--");
		assertEquals("MarkLogicBogusCA", hostName);
	}

	@Test
	void unexpectedEvalResponse() {
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
	void anotherUnexpectedEvalResponse() {
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
	void extractHostNameFromFile() {
		File certFile = new File("src/test/resources/sample-app/host-certificates/security/certificate-templates" +
			"/host-certificates/sample-app-certificate-template/host1.marklogic.com.crt");
		String hostName = new InsertCertificateHostsTemplateCommand().getCertificateHostName(certFile, manageClient);
		assertEquals("host1.marklogic.com", hostName);
	}

	@Test
	void insertCertificateAndVerify() {
		appConfig.setConfigDir(new ConfigDir(new File("src/test/resources/sample-app/host-certificates")));

		initializeAppDeployer(
			new DeployCertificateTemplatesCommand(),
			new InsertCertificateHostsTemplateCommand()
		);

		CertificateTemplateManager mgr = new CertificateTemplateManager(manageClient);

		try {
			deploySampleApp();
			verifyHostCertificateWasInserted(mgr);

			// Make sure nothing breaks by deploying it again
			deploySampleApp();
			verifyHostCertificateWasInserted(mgr);
		} finally {
			undeploySampleApp();

			List<String> templateNames = mgr.getAsXml().getListItemNameRefs();
			assertFalse(templateNames.contains(TEMPLATE_NAME));
		}
	}

	@Test
	@ExtendWith(RequiresMarkLogic12.class)
	void encryptedPrivateKey() {
		appConfig.setConfigDir(new ConfigDir(new File("src/test/resources/sample-app/encrypted-private-key")));
		appConfig.setHostCertificatePassphrases(Map.of("host3.marklogic.com", "password"));

		initializeAppDeployer(
			new DeployCertificateTemplatesCommand(),
			new InsertCertificateHostsTemplateCommand()
		);

		CertificateTemplateManager mgr = new CertificateTemplateManager(manageClient);

		try {
			deploySampleApp();
			verifyHostCertificateWasInserted(mgr);
		} finally {
			undeploySampleApp();
		}
	}

	@Test
	@ExtendWith(RequiresMarkLogic12.class)
	void encryptedPrivateKeyWithWrongPassphrase() {
		appConfig.setConfigDir(new ConfigDir(new File("src/test/resources/sample-app/encrypted-private-key")));
		appConfig.setHostCertificatePassphrases(Map.of("host3.marklogic.com", "wrong-passphrase"));

		initializeAppDeployer(
			new DeployCertificateTemplatesCommand(),
			new InsertCertificateHostsTemplateCommand()
		);

		try {
			deploySampleApp();
		} catch (HttpClientErrorException ex) {
			assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
			assertTrue(ex.getMessage().contains("PKI-BADPRIVATEKEY"), "Expected insertion of the host certificate to fail " +
				"due to the invalid passphrase. Actual error message: " + ex.getMessage());
		} finally {
			undeploySampleApp();
		}
	}

	@Test
	@ExtendWith(RequiresMarkLogic12.class)
	void encryptedPrivateKeyWithNoPassphrase() {
		appConfig.setConfigDir(new ConfigDir(new File("src/test/resources/sample-app/encrypted-private-key")));

		initializeAppDeployer(
			new DeployCertificateTemplatesCommand(),
			new InsertCertificateHostsTemplateCommand()
		);

		try {
			deploySampleApp();
		} catch (HttpClientErrorException ex) {
			assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
			assertTrue(ex.getMessage().contains("PKI-PRIVATEKEYMISSINGPASSPHRASE"), "Expected insertion of the host certificate to fail " +
				"due no passphrase being provided. Actual error message: " + ex.getMessage());
		} finally {
			undeploySampleApp();
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
