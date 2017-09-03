package com.marklogic.appdeployer.export.impl;

import com.marklogic.appdeployer.export.ExportedResources;
import com.marklogic.appdeployer.export.impl.AbstractResourceExporter;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.resource.ResourceManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Can use this as a parent class when exporting a resource that can be referred to via a single name, and when that
 * name makes sense to use as the name of the file that's exported.
 */
public abstract class AbstractNamedResourceExporter extends AbstractResourceExporter {

	private String[] resourceNames;

	protected AbstractNamedResourceExporter(ManageClient manageClient, String... resourceNames) {
		super(manageClient);
		this.resourceNames = resourceNames;
	}

	protected abstract ResourceManager newResourceManager(ManageClient manageClient);

	protected abstract File getResourceDirectory(File baseDir);

	@Override
	public ExportedResources exportResources(File baseDir) {
		List<File> files = new ArrayList<>();
		if (resourceNames != null && resourceNames.length > 0) {
			ResourceManager mgr = newResourceManager(getManageClient());
			File resourceDir = getResourceDirectory(baseDir);
			resourceDir.mkdirs();
			for (String resourceName : resourceNames) {
				File f = exportToFile(mgr, resourceName, resourceDir);
				if (f != null) {
					files.add(f);
				}
			}
		}
		return new ExportedResources(files, getExportMessages());
	}

	protected String[] getExportMessages() {
		return null;
	}

	public String[] getResourceNames() {
		return resourceNames;
	}
}
