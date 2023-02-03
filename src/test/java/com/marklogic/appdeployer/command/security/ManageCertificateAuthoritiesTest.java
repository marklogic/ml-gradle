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
import com.marklogic.appdeployer.command.ResourceFilenameFilter;
import com.marklogic.mgmt.resource.security.CertificateAuthorityManager;
import com.marklogic.rest.util.ResourcesFragment;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

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
		assertNotNull(id, "The certificate authority should have been created");

		// Delete the certificate authority
		mgr.delete(id);

		// And then verify that it's gone
		resources = mgr.getAsXml();
		id = resources.getListItemValue("MarkLogic TX Engineering", "idref");
		assertNull(id, "The certificate authority should no longer exist");
	}

	@Test
	public void verifyFileExtensions() {
		DeployCertificateAuthoritiesCommand command = new DeployCertificateAuthoritiesCommand();
		ResourceFilenameFilter filter = (ResourceFilenameFilter) command.getResourceFilenameFilter();
		Set<String> extensions = filter.getSupportedFilenameExtensions();

		for (String extension : new String[]{".cer", ".crt", ".der", ".p12", ".p7b", ".p7r", ".pem", ".pfx", ".spc"}) {
			assertTrue(extensions.contains(extension));
		}
	}
}
