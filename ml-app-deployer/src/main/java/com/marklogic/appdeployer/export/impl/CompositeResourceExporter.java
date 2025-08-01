/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.export.impl;

import com.marklogic.appdeployer.export.ExportedResources;
import com.marklogic.appdeployer.export.ResourceExporter;
import com.marklogic.appdeployer.export.impl.AbstractResourceExporter;
import com.marklogic.client.ext.helper.LoggingObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Lets you combine many instances of ResourceExporter and invoke them all at once (could parallelize this in the
 * future).
 */
public class CompositeResourceExporter extends LoggingObject implements ResourceExporter {

	private List<ResourceExporter> resourceExporters;
	private boolean overrideFormatOnExporters = true;
	private String format = FORMAT_JSON;

	public CompositeResourceExporter(ResourceExporter... resourceExporters) {
		this.resourceExporters = new ArrayList<>();
		this.resourceExporters.addAll(Arrays.asList(resourceExporters));
	}

	public void add(ResourceExporter exporter) {
		this.resourceExporters.add(exporter);
	}

	@Override
	public ExportedResources exportResources(File baseDir) {
		ExportedResources resources = null;
		for (ResourceExporter exporter : resourceExporters) {
			if (overrideFormatOnExporters && exporter instanceof AbstractResourceExporter) {
				((AbstractResourceExporter) exporter).setFormat(getFormat());
			}
			ExportedResources er = exporter.exportResources(baseDir);
			if (resources == null) {
				resources = er;
			} else {
				resources.add(er);
			}
		}
		if (resources == null) {
			resources = new ExportedResources(new ArrayList<>(), new String[]{});
		}
		return resources;
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

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}
}
