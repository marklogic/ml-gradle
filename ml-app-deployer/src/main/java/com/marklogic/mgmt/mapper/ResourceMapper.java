/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt.mapper;

import com.marklogic.mgmt.api.Resource;

/**
 * The eventual plan is for this to have both read and write methods. Immediate need is only for a read method.
 */
public interface ResourceMapper {

	/**
	 * The payload is defined as a string so that the implementation can easily determine whether the payload is
	 * JSON or XML.
	 *
	 * @param payload
	 * @param type
	 * @param <T>
	 * @return
	 */
	<T extends Resource> T readResource(String payload, Class<T> type);

}
