package com.marklogic.appdeployer.export;

import com.marklogic.appdeployer.export.security.RoleExporter;
import com.marklogic.appdeployer.export.security.UserExporter;
import com.marklogic.mgmt.ManageClient;

import java.io.File;
import java.util.List;

/**
 * Intent is to provide a fluent-ish way of composing comands for exporting resources.
 */
public class Exporter {

	private CompositeResourceExporter compositeExporter;
	private ManageClient manageClient;

	public static Exporter client(ManageClient manageClient) {
		return new Exporter(manageClient);
	}

	public Exporter(ManageClient manageClient) {
		this.manageClient = manageClient;
		compositeExporter = new CompositeResourceExporter(manageClient);
	}

	public Exporter format(String xmlOrJson) {
		compositeExporter.setFormat(xmlOrJson);
		return this;
	}

	public List<File> export(File baseDir) {
		return compositeExporter.exportResources(baseDir);
	}

	public Exporter users(String... usernames) {
		compositeExporter.add(new UserExporter(manageClient, usernames));
		return this;
	}

	public Exporter roles(String... roleNames) {
		compositeExporter.add(new RoleExporter(manageClient, roleNames));
		return this;
	}

}
