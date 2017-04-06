package com.marklogic.appdeployer.export;

import com.marklogic.mgmt.ManageClient;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Lets you combine many instances of ResourceExporter and invoke them all at once (could parallelize this in the
 * future).
 */
public class CompositeResourceExporter extends AbstractResourceExporter {

	private List<ResourceExporter> resourceExporters;
	private boolean overrideFormatOnExporters = true;

	public CompositeResourceExporter(ManageClient manageClient, ResourceExporter... resourceExporters) {
		super(manageClient);
		this.resourceExporters = new ArrayList<>();
		for (ResourceExporter exporter : resourceExporters) {
			this.resourceExporters.add(exporter);
		}
	}

	public void add(ResourceExporter exporter) {
		this.resourceExporters.add(exporter);
	}

	@Override
	public List<File> exportResources(File baseDir) {
		List<File> files = new ArrayList<>();
		for (ResourceExporter exporter : resourceExporters) {
			if (overrideFormatOnExporters && exporter instanceof AbstractResourceExporter) {
				((AbstractResourceExporter) exporter).setFormat(getFormat());
			}
			files.addAll(exporter.exportResources(baseDir));
		}
		return files;
	}

	public boolean isOverrideFormatOnExporters() {
		return overrideFormatOnExporters;
	}

	public void setOverrideFormatOnExporters(boolean overrideFormatOnExporters) {
		this.overrideFormatOnExporters = overrideFormatOnExporters;
	}

	public List<ResourceExporter> getResourceExporters() {
		return resourceExporters;
	}

	public void setResourceExporters(List<ResourceExporter> resourceExporters) {
		this.resourceExporters = resourceExporters;
	}
}
