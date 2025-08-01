/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.ext.datamovement.util;

import com.marklogic.client.document.ServerTransform;

public abstract class TransformPropertyValueParser {

	/**
	 * Utility method for parsing a value which may have transform parameters appended - e.g.
	 * myTransform,param1,value1,param2,value2.
	 *
	 * @param value
	 * @return
	 */
	public static ServerTransform parsePropertyValue(String value) {
		String[] tokens = value.split(",");
		ServerTransform transform = new ServerTransform(tokens[0]);
		for (int i = 1; i < tokens.length; i += 2) {
			transform.addParameter(tokens[i], tokens[i + 1]);
		}
		return transform;
	}
}
