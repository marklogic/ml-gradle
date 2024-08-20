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

/**
 * Defines the inputs needed for exporting a resource.
 */
public interface ExportInputs {

	/**
	 * The resource name is used along with the resource URL parameters to retrieve the properties for a resource.
	 *
	 * @return
	 */
	String getResourceName();

	/**
	 * Return an array of URL parameters for getting the properties of a resource, where the array consists of parameter
	 * name, parameter value, etc. Can return an empty array.
	 *
	 * @return
	 */
	String[] getResourceUrlParams();

	/**
	 * Build the filename for the resource identified by this interface, using the given suffix.
	 *
	 * @param suffix
	 * @return
	 */
	String buildFilename(String suffix);
}
