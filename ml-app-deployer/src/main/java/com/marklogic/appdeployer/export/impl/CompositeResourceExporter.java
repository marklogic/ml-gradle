/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
