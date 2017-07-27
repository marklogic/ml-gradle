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
