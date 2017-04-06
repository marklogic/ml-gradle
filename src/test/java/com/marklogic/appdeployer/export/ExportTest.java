package com.marklogic.appdeployer.export;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import org.junit.Test;

import java.io.File;
import java.util.List;

public class ExportTest extends AbstractAppDeployerTest {

	@Test
	public void test() throws Exception {

		// Define the directory to export to
		File baseDir = new File("build/export-test");
		baseDir.mkdirs();

		List<File> exportedFiles =
			Exporter.client(manageClient)
			.users("admin", "nobody")
			.roles("admin", "rest-admin", "rest-reader")
			.format("xml")
			.export(baseDir);

		// Export!
		System.out.println(exportedFiles);
	}
}
