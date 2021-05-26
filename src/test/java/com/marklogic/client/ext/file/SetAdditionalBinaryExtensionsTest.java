package com.marklogic.client.ext.file;

import com.marklogic.client.ext.AbstractIntegrationTest;
import com.marklogic.client.io.Format;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SetAdditionalBinaryExtensionsTest extends AbstractIntegrationTest {

	@Test
	public void test() {
		client = newClient("Documents");
		client.newServerEval().xquery("cts:uris((), (), cts:true-query()) ! xdmp:document-delete(.)").eval();

		GenericFileLoader loader = new GenericFileLoader(client);
		loader.setAdditionalBinaryExtensions("test1", "test2");
		List<DocumentFile> files = loader.loadFiles(Paths.get("src", "test", "resources", "binary-test").toString());
		for (DocumentFile file : files) {
			String name = file.getFile().getName();
			if ("file.test1".equals(name) || "file.test2".equals(name)) {
				assertEquals(Format.BINARY, file.getFormat());
			} else {
				assertEquals(Format.TEXT, file.getFormat(), "Unrecognized extensions should default to text");
			}
		}
	}
}
