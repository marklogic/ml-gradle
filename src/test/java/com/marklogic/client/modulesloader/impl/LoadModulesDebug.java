package com.marklogic.client.modulesloader.impl;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import org.springframework.util.FileCopyUtils;

import java.io.File;

/**
 * Debug program to testing module loading.
 */
public class LoadModulesDebug {

	public static void main(String[] args) throws Exception {
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8000, "admin", "admin",
			Authentication.DIGEST);

		DatabaseClient modulesClient = DatabaseClientFactory.newClient("localhost", 8000, "Modules", "admin", "admin",
			Authentication.DIGEST);

		RestApiAssetLoader raal = new RestApiAssetLoader(modulesClient);
		XccAssetLoader xal = new XccAssetLoader();
		xal.setHost("localhost");
		xal.setDatabaseName("Modules");
		xal.setPassword("admin");
		xal.setUsername("admin");
		xal.setPort(8000);

		DefaultModulesLoader l = new DefaultModulesLoader();
		l.setModulesManager(null);
		l.setCatchExceptions(true);
		//l.setRestApiAssetLoader(raal);
		l.setXccAssetLoader(xal);

		String path = "src/test/resources/sample-base-dir";
		try {
			long start = System.currentTimeMillis();
			l.loadModules(new File(path), new DefaultModulesFinder(), client);
			System.out.println("Time: " + (System.currentTimeMillis() - start));
		} finally {
			client.release();
			modulesClient.release();
		}
	}
}
