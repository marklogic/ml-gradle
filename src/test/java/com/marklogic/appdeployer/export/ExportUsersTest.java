package com.marklogic.appdeployer.export;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.rest.util.Fragment;
import org.junit.jupiter.api.Test;
import org.springframework.util.FileCopyUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExportUsersTest extends AbstractExportTest {

	@Test
	public void exportAsJson() throws Exception {
		ExportedResources resources = new Exporter(manageClient).users("admin", "healthcheck").export(exportDir);

		assertEquals("The exported user files each have a default password in them, as the real password cannot be exported for security reasons.",
			resources.getMessages().get(0));

		JsonNode json = objectMapper.readTree(resources.getFiles().get(0));
		assertEquals("admin", json.get("user-name").asText());
		assertEquals("CHANGEME", json.get("password").asText());

		json = objectMapper.readTree(resources.getFiles().get(1));
		assertEquals("healthcheck", json.get("user-name").asText());
		assertEquals("CHANGEME", json.get("password").asText());
	}

	@Test
	public void exportAsXml() throws Exception {
		ExportedResources resources = new Exporter(manageClient).format("xml").users("admin", "healthcheck").export(exportDir);

		assertEquals("The exported user files each have a default password in them, as the real password cannot be exported for security reasons.",
			resources.getMessages().get(0));

		Fragment xml = new Fragment(new String(FileCopyUtils.copyToByteArray(resources.getFiles().get(0))));
		assertEquals("admin", xml.getElementValue("/node()/m:user-name"));
		assertEquals("CHANGEME", xml.getElementValue("/node()/m:password"));

		xml = new Fragment(new String(FileCopyUtils.copyToByteArray(resources.getFiles().get(1))));
		assertEquals("healthcheck", xml.getElementValue("/node()/m:user-name"));
		assertEquals("CHANGEME", xml.getElementValue("/node()/m:password"));
	}
}
