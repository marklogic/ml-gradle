package com.marklogic.appdeployer.export;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.resource.ResourceManager;
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
	private ObjectMapper objectMapper;

	protected AbstractNamedResourceExporter(ManageClient manageClient, String... resourceNames) {
		super(manageClient);
		this.resourceNames = resourceNames;
		this.objectMapper = new ObjectMapper();
	}

	protected abstract ResourceManager newResourceManager(ManageClient manageClient);

	protected abstract File getResourceDirectory(File baseDir);

	@Override
	public ExportedResources exportResources(File baseDir) {
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
		return new ExportedResources(files, getExportMessages());
	}

	protected String[] getExportMessages() {
		return null;
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
		String xml = mgr.getPropertiesAsXmlString(resourceName, getResourceUrlParams(resourceName));
		xml = beforeResourceWrittenToFile(resourceName, xml);
		File f = new File(resourceDir, buildFilename(resourceName, "xml"));
		logWritingFile(resourceName, f);
		FileCopyUtils.copy(xml.getBytes(), f);
		return f;
	}

	protected String buildFilename(String resourceName, String suffix) {
		return resourceName + "." + suffix;
	}

	/**
	 * Subclasses that need to provide URL params for getting the properties of a resource can override this.
	 *
	 * @param resourceName
	 * @return
	 */
	protected String[] getResourceUrlParams(String resourceName) {
		return null;
	}

	protected File exportToJson(ResourceManager mgr, String resourceName, File resourceDir) throws IOException {
		String json = mgr.getPropertiesAsJson(resourceName, getResourceUrlParams(resourceName));
		json = beforeResourceWrittenToFile(resourceName, json);
		json = prettyPrintJson(json);

		File f = new File(resourceDir, buildFilename(resourceName, "json"));
		logWritingFile(resourceName, f);
		FileCopyUtils.copy(json.getBytes(), f);
		return f;
	}

	protected String prettyPrintJson(String json) throws IOException {
		JsonNode node = objectMapper.readTree(json);
		return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(node);
	}

	protected String beforeResourceWrittenToFile(String resourceName, String payload) {
		return payload;
	}

	protected void logWritingFile(String resourceName, File file) {
		if (logger.isInfoEnabled()) {
			logger.info(format("Exporting resource %s to file %s", resourceName, file.getAbsolutePath()));
		}
	}

	public String[] getResourceNames() {
		return resourceNames;
	}
}
