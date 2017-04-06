package com.marklogic.appdeployer.export;

import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.ResourceManager;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.IOException;
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
	public List<File> exportResources(File baseDir) {
		ResourceManager mgr = newResourceManager(getManageClient());
		File resourceDir = getResourceDirectory(baseDir);
		resourceDir.mkdirs();
		List<File> files = new ArrayList<>();
		for (String resourceName : resourceNames) {
			File f = exportToFile(mgr, resourceName, resourceDir);
			if (f != null) {
				files.add(f);
			}
		}
		return files;
	}

	protected File exportToFile(ResourceManager mgr, String resourceName, File resourceDir) {
		File f = null;
		try {
			if (isFormatXml()) {
				f = exportToXml(mgr, resourceName, resourceDir);
			} else {
				f = exportToJson(mgr, resourceName, resourceDir);
			}
		} catch (IOException ex) {
			logger.warn(format("Unable to export resource with name %s to resource directory %s, cause: %s",
				resourceName, resourceDir.getAbsolutePath(), ex.getMessage()), ex);
		}
		return f;
	}

	protected File exportToXml(ResourceManager mgr, String resourceName, File resourceDir) throws IOException {
		String xml = mgr.getPropertiesAsXml(resourceName).getPrettyXml();
		File f = new File(resourceDir, resourceName + ".xml");
		logWritingFile(resourceName, f);
		FileCopyUtils.copy(xml.getBytes(), f);
		return f;
	}

	protected File exportToJson(ResourceManager mgr, String resourceName, File resourceDir) throws IOException {
		String json = mgr.getPropertiesAsJson(resourceName);
		File f = new File(resourceDir, resourceName + ".json");
		logWritingFile(resourceName, f);
		FileCopyUtils.copy(json.getBytes(), f);
		return f;
	}

	protected void logWritingFile(String resourceName, File file) {
		if (logger.isInfoEnabled()) {
			logger.info(format("Exporting resource %s to file %s", resourceName, file.getAbsolutePath()));
		}
	}

}
