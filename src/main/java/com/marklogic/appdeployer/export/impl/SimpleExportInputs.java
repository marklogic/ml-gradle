package com.marklogic.appdeployer.export.impl;

import com.marklogic.appdeployer.export.impl.ExportInputs;

/**
 * Simple implementation that works well for resources that are identified by their resource name along with an
 * optional set of URL parameters for retrieving the resource's properties.
 */
public class SimpleExportInputs implements ExportInputs {

	private String resourceName;
	private String[] resourceUrlParams;

	public SimpleExportInputs(String resourceName, String... resourceUrlParams) {
		this.resourceName = resourceName;
		this.resourceUrlParams = resourceUrlParams;
	}

	@Override
	public String getResourceName() {
		return resourceName;
	}

	@Override
	public String[] getResourceUrlParams() {
		return resourceUrlParams;
	}

	@Override
	public String buildFilename(String suffix) {
		return resourceName + "." + suffix;
	}
}
