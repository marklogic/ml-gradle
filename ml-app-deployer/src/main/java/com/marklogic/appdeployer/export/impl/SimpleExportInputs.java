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
