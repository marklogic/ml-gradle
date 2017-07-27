package com.marklogic.appdeployer.export;

import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.api.API;
import com.marklogic.mgmt.api.security.Amp;
import com.marklogic.mgmt.mapper.DefaultResourceMapper;
import com.marklogic.mgmt.mapper.ResourceMapper;
import com.marklogic.mgmt.security.AmpManager;
import com.marklogic.mgmt.selector.PropertiesResourceSelector;
import com.marklogic.mgmt.selector.RegexResourceSelector;
import com.marklogic.mgmt.selector.ResourceSelector;
import org.junit.Test;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.util.List;
import java.util.Properties;

public class ExportAmpsTest extends AbstractExportTest {

	@Test
	public void test() throws Exception {
		String amp1 = "{\n" +
			"  \"namespace\": \"http://example.com/uri\",\n" +
			"  \"local-name\": \"abctest-amp\",\n" +
			"  \"document-uri\": \"/module/path/name\",\n" +
			"  \"modules-database\": \"Documents\",\n" +
			"  \"role\": [\"rest-writer\"]\n" +
			"}";

		String amp2 = "{\n" +
			"  \"namespace\": \"http://example.com/uri\",\n" +
			"  \"local-name\": \"abctest-amp\",\n" +
			"  \"document-uri\": \"/module/path/name\",\n" +
			"  \"modules-database\": \"Modules\",\n" +
			"  \"role\": [\"rest-writer\"]\n" +
			"}";

		ManageClient client = new ManageClient();
		AmpManager mgr = new AmpManager(client);

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
		props.put("amps", "/manage/v2/amps/abctest-amp?namespace=http://example.com/uri&document-uri=/module/path/name&modules-database=Documents," +
			"/manage/v2/amps/abctest-amp?namespace=http://example.com/uri&document-uri=/module/path/name&modules-database=Modules");
		PropertiesResourceSelector selector = new PropertiesResourceSelector(props);
		verifyAmpsCanBeExported(selector);
	}

	private void verifyAmpsCanBeExported(ResourceSelector selector) throws Exception {
		ExportedResources exports = new Exporter(manageClient).select(selector).export(exportDir);

		List<File> files = exports.getFiles();
		assertEquals(2, files.size());

		// Kick the tires on our JSON annotations on Amp
		ResourceMapper mapper = new DefaultResourceMapper(new API(manageClient));

		Amp amp = mapper.readResource(new String(FileCopyUtils.copyToByteArray(files.get(0))), Amp.class);
		assertEquals("http://example.com/uri", amp.getNamespace());
		assertEquals("abctest-amp", amp.getLocalName());
		assertEquals("/module/path/name", amp.getDocumentUri());
		assertEquals("Documents", amp.getModulesDatabase());

		amp = mapper.readResource(new String(FileCopyUtils.copyToByteArray(files.get(1))), Amp.class);
		assertEquals("http://example.com/uri", amp.getNamespace());
		assertEquals("abctest-amp", amp.getLocalName());
		assertEquals("/module/path/name", amp.getDocumentUri());
		assertEquals("Modules", amp.getModulesDatabase());
	}
}
