/*
 * Copyright © 2025 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.rest.util;

public abstract class XPathUtil {

	/**
	 * Sanitize the resource ID or name so it can be safely included as a value in an XPath expression.
	 *
	 * @param resourceIdOrName
	 * @return
	 */
	public static String sanitizeValueForXPathExpression(String resourceIdOrName) {
		// MarkLogic generally the following characters in a resource name, and we know an ID will never have them.
		// Removing them avoids issues with XPath queries.
		return resourceIdOrName != null ?
			resourceIdOrName.replace("'", "").replace("\"", "").replace("[", "").replace("]", "") :
			null;
	}

	private XPathUtil() {
		// Prevent instantiation
	}
}
