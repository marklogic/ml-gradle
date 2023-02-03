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
package com.marklogic.appdeployer.export;

import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.api.API;
import com.marklogic.mgmt.api.security.Amp;
import com.marklogic.mgmt.mapper.DefaultResourceMapper;
import com.marklogic.mgmt.mapper.ResourceMapper;
import com.marklogic.mgmt.resource.security.AmpManager;
import com.marklogic.mgmt.selector.PropertiesResourceSelector;
import com.marklogic.mgmt.selector.RegexResourceSelector;
import com.marklogic.mgmt.selector.ResourceSelector;
import org.junit.jupiter.api.Test;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class ExportAmpsTest extends AbstractExportTest {

	@Test
	public void test() throws Exception {
		String amp1 = "{\n" +
			"  \"namespace\": \"http://example.com/uri\",\n" +
			"  \"local-name\": \"abctest-docs-amp\",\n" +
			"  \"document-uri\": \"/module/path/name\",\n" +
			"  \"modules-database\": \"Documents\",\n" +
			"  \"role\": [\"rest-writer\"]\n" +
			"}";

		String amp2 = "{\n" +
			"  \"namespace\": \"http://example.com/uri\",\n" +
			"  \"local-name\": \"abctest-mods-amp\",\n" +
			"  \"document-uri\": \"/module/path/name\",\n" +
			"  \"modules-database\": \"Modules\",\n" +
			"  \"role\": [\"rest-writer\"]\n" +
			"}";

		AmpManager mgr = new AmpManager(super.manageClient);

		try {
			mgr.save(amp1);
			mgr.save(amp2);
			assertTrue(mgr.ampExists(amp1));
			assertTrue(mgr.ampExists(amp2));

			verifyAmpsCanBeExportedViaRegex();
			initializeExportDir();
			verifyAmpsCanBeExportedViaProperties();
		} finally {
			mgr.delete(amp1);
			mgr.delete(amp2);
		}
	}

	private void verifyAmpsCanBeExportedViaRegex() throws Exception {
		RegexResourceSelector selector = new RegexResourceSelector("abctest.*");
		verifyAmpsCanBeExported(selector);
	}

	private void verifyAmpsCanBeExportedViaProperties() throws Exception {
		Properties props = new Properties();
		props.put("amps", "/manage/v2/amps/abctest-docs-amp?namespace=http://example.com/uri&document-uri=/module/path/name&modules-database=Documents," +
			"/manage/v2/amps/abctest-mods-amp?namespace=http://example.com/uri&document-uri=/module/path/name&modules-database=Modules");
		PropertiesResourceSelector selector = new PropertiesResourceSelector(props);
		verifyAmpsCanBeExported(selector);
	}

	private void verifyAmpsCanBeExported(ResourceSelector selector) throws Exception {
		ExportedResources exports = new Exporter(manageClient).select(selector).export(exportDir);

		List<File> files = exports.getFiles();
		assertEquals(2, files.size());

		// Kick the tires on our JSON annotations on Amp
		ResourceMapper mapper = new DefaultResourceMapper(new API(manageClient));

		// order of amps is not very reliable
		for (File file : files) {
			Amp amp = mapper.readResource(new String(FileCopyUtils.copyToByteArray(files.get(0))), Amp.class);
			assertEquals("http://example.com/uri", amp.getNamespace());
			assertEquals("/module/path/name", amp.getDocumentUri());

			if (amp.getLocalName().equals("abctest-docs-amp")) {
				assertEquals("Documents", amp.getModulesDatabase());
			}
			else if (amp.getLocalName().equals("abctest-mods-amp")) {
				assertEquals("Modules", amp.getModulesDatabase());
			}
			else {
				fail("Invalid amp name");
			}
		}
	}
}
